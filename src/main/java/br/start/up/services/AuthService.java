package br.start.up.services;

import br.start.up.detail.UserDetail;
import br.start.up.dtos.request.AuthRequestDTO;
import br.start.up.dtos.summary.AccountSummaryDTO;
import br.start.up.dtos.summary.AccountSummaryWithTokenAccessDTO;
import br.start.up.jwt.JwtService;
import br.start.up.model.Account;
import br.start.up.model.Profile;
import br.start.up.repository.AccountRepository;
import br.start.up.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private CodeResetPasswordService codeResetPasswordService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Authentication authentication;

    @Autowired
    private N8NService n8NService;

    private final ModelMapper mapper = new ModelMapper();

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
        Account account = repository.findByEmail(email);

        if(account != null && account.getProfile() != null){
            var code = codeResetPasswordService.generate();
            codeResetPasswordService.setCode(email, code);
            n8NService.send(account.getEmail(), account.getProfile().getUsername(), code);
        }
    }

    public boolean verifyCodeResetPassword(String email, String code) {
        return codeResetPasswordService.verifyCode(email, code);
    }

    public AccountSummaryDTO read() {
        return mapper.map(repository.findByEmail((String) authentication.getPrincipal()), AccountSummaryDTO.class);
    }
}
