package br.start.up.services;

import br.start.up.detail.UserAuthLoader;
import br.start.up.detail.UserDetail;
import br.start.up.dtos.cache.AuthCacheDTO;
import br.start.up.dtos.request.AuthRequestDTO;
import br.start.up.dtos.request.CreateAccountDTO;
import br.start.up.dtos.request.UpdateAccountDTO;
import br.start.up.dtos.summary.AccountSummaryDTO;
import br.start.up.dtos.summary.AccountSummaryWithTokenAccessDTO;
import br.start.up.jwt.JwtService;
import br.start.up.model.Account;
import br.start.up.model.AuthorityByRole;
import br.start.up.model.Profile;
import br.start.up.repository.AccountRepository;
import br.start.up.repository.AuthorityByRoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserAuthLoader {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AuthorityByRoleRepository authorityRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private DashboardMetricsService dashboardMetricsService;

    private final ModelMapper mapper = new ModelMapper();

    public AccountSummaryDTO create(CreateAccountDTO account) {
        Account acc = Account.builder()
                .profile(Profile.builder().username(account.getUsername()).build())
                .username(account.getUsername())
                .email(account.getEmail())
                .password(encoder.encode(account.getPassword()))
                .role("user")
                .authorities(authorityRepository.findAllByRole("user").stream().map(AuthorityByRole::getAuthority).collect(Collectors.toSet()))
                .active(true)
                .createAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        var created = repository.save(acc);

        dashboardMetricsService.incrementNewUser();

        return mapper.map(created, AccountSummaryDTO.class);
    }

    public AccountSummaryDTO update(Long id, UpdateAccountDTO account) {
        Account acc_ = repository.findById(id).orElseThrow(() -> notFound(id));

        Account acc = acc_.toBuilder()
                .username(account.getUsername())
                .profile(acc_.getProfile().toBuilder().username(account.getUsername()).build())
                .password(encoder.encode(account.getPassword()))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        return mapper.map(acc, AccountSummaryDTO.class);
    }

    public AccountSummaryDTO read(Long id) {
        Account acc_ = repository.findById(id).orElseThrow(() -> notFound(id));
        return mapper.map(acc_, AccountSummaryDTO.class);
    }

    public Page<AccountSummaryDTO> readAllByPageable(Pageable pageable) {
        return repository.findAll(pageable).map(a -> mapper.map(a, AccountSummaryDTO.class));
    }

    public void resetPassword(String email, String newPassword) {
        Account acc_ = Optional.of(repository.findByEmail(email)).orElseThrow(() -> notFound(email));
        Account account = acc_.toBuilder()
                .password(encoder.encode(newPassword))
                .updateAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();
        repository.save(account);
    }

    public void resetAuthoritiesById(Long id){
        Account account = repository.findById(id).orElseThrow(() -> notFound(id));
        repository.save(account.toBuilder().authorities(authorityRepository.findAllByRole("user").stream().map(AuthorityByRole::getAuthority).collect(Collectors.toSet())).build());
    }

    public Page<AccountSummaryDTO> search(String term, Pageable pageable) {
        if(term.startsWith("email:")){
            return repository.findAllByEmailContainingIgnoreCase(term.substring(6), pageable).map(a -> mapper.map(a, AccountSummaryDTO.class));
        }
        return repository.search(term, pageable).map(a -> mapper.map(a, AccountSummaryDTO.class));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Account by id %d not found".formatted(id));
    }

    private ResponseStatusException notFound(String email) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Account by email %s not found".formatted(email));
    }

    public Long count() {
        return repository.count();
    }

    @Cacheable(
            value = "user-auth-cache",
            key = "#userId",
            unless = "#result == null"
    )
    @Transactional
    public AuthCacheDTO loadUserById(Long userId) {
        var account = repository.findById(userId).orElseThrow(() -> notFound(userId));

        Set<String> authorities = account.getAuthorities();
        authorities.add(account.getRole());

        return AuthCacheDTO.builder()
                .id(account.getId())
                .principal(account.getEmail())
                .authorities(authorities).build();
    }
}
