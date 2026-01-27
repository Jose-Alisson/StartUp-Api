package br.start.up.services;

import br.start.up.detail.UserDetail;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AuthorityByRoleRepository authorityRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private Authentication authentication;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private CodeResetPasswordService codeResetPasswordService;

    @Autowired
    private JwtService jwtService;

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

        return mapper.map(repository.save(acc), AccountSummaryDTO.class);
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

    public AccountSummaryDTO read() {
        return mapper.map(repository.findByEmail((String) authentication.getPrincipal()), AccountSummaryDTO.class);
    }

    public AccountSummaryDTO read(Long id) {
        Account acc_ = repository.findById(id).orElseThrow(() -> notFound(id));
        return mapper.map(acc_, AccountSummaryDTO.class);
    }

    public Page<AccountSummaryDTO> readAllByPageable(Pageable pageable) {
        return repository.findAll(pageable).map(a -> mapper.map(a, AccountSummaryDTO.class));
    }

    public AccountSummaryWithTokenAccessDTO login(AuthRequestDTO auth) {

        var authenticate = authManager.authenticate(new UsernamePasswordAuthenticationToken(auth.getEmail(), auth.getPassword()));

        UserDetail detail = (UserDetail) authenticate.getPrincipal();

        return AccountSummaryWithTokenAccessDTO
                .builder()
                .account(mapper.map(detail.getAccount(), AccountSummaryDTO.class))
                .token(jwtService.generateJwt(detail))
                .build();
    }

    public void sendCodeResetPassword(String email) {
        codeResetPasswordService.setCode(email, codeResetPasswordService.generate());
    }

    public boolean verifyCodeResetPassword(String email, String code) {
        return codeResetPasswordService.verifyCode(email, code);
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

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Account by id %d not found".formatted(id));
    }

    private ResponseStatusException notFound(String email) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "The Account by email %s not found".formatted(email));
    }
}
