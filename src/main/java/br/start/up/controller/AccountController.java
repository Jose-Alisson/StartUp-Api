package br.start.up.controller;

import br.start.up.dtos.request.AuthRequestDTO;
import br.start.up.dtos.request.CreateAccountDTO;
import br.start.up.dtos.request.RequestResetPasswordDTO;
import br.start.up.dtos.request.UpdateAccountDTO;
import br.start.up.dtos.summary.AccountSummaryDTO;
import br.start.up.dtos.summary.AccountSummaryWithTokenAccessDTO;
import br.start.up.services.AccountService;
import br.start.up.services.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public AccountSummaryWithTokenAccessDTO login(@Valid @RequestBody AuthRequestDTO requestDTO){
        return authService.login(requestDTO);
    }

    @PatchMapping("/{email}/send-reset-password")
    public void sendResetPassword(@PathVariable("email") String email){
        authService.sendCodeResetPassword(email);
    }

    @PatchMapping("/{email}/verify-code-reset-password")
    public ResponseEntity<?> verifyCodeResetPassword(@PathVariable("email") String email, @RequestBody Map<String, String> body){
        var verify = authService.verifyCodeResetPassword(email, body.get("code"));

        if(verify){
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    @PatchMapping("/{email}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable("email") String email, @Valid @RequestBody RequestResetPasswordDTO resetPasswordDTO){
        if(authService.verifyCodeResetPassword(email, resetPasswordDTO.getCode())){
            service.resetPassword(email, resetPasswordDTO.getPassword());
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(401).body("Infelizmente código não correspondente");
    }

    @PostMapping("/create")
    public AccountSummaryDTO create(@Valid @RequestBody CreateAccountDTO account){
        return service.create(account);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'manager'")
    @PutMapping("/{id}/update")
    public AccountSummaryDTO update(Long id, @RequestBody UpdateAccountDTO account){
        return service.update(id, account);
    }

    @GetMapping("/{id}/")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public AccountSummaryDTO read(Long id){
        return service.read(id);
    }

    @GetMapping("/me")
    public AccountSummaryDTO readMe(){
        return authService.read();
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('manager','admin')")
    public Page<AccountSummaryDTO> readAll(Pageable pageable){
        return service.readAllByPageable(pageable);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public Page<AccountSummaryDTO> search(
            @RequestParam(value = "term", required = false) String term,
            Pageable pageable
    ){
        if(term != null && !term.isBlank()) {
            return service.search(term, pageable);
        }
        return service.readAllByPageable(pageable);
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public Long getCount(){
        return service.count();
    }
}
