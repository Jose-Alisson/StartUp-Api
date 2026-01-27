package br.start.up.controller;

import br.start.up.dtos.request.AuthRequestDTO;
import br.start.up.dtos.request.CreateAccountDTO;
import br.start.up.dtos.request.RequestResetPasswordDTO;
import br.start.up.dtos.request.UpdateAccountDTO;
import br.start.up.dtos.summary.AccountSummaryDTO;
import br.start.up.dtos.summary.AccountSummaryWithTokenAccessDTO;
import br.start.up.services.AccountService;
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

    @PostMapping("/login")
    public AccountSummaryWithTokenAccessDTO login(@Valid @RequestBody AuthRequestDTO requestDTO){
        return service.login(requestDTO);
    }

    @PatchMapping("/{email}/send-reset-password")
    public void sendResetPassword(@PathVariable("email") String email){
        service.sendCodeResetPassword(email);
    }

    @PatchMapping("/{email}/verify-code-reset-password")
    public ResponseEntity<?> verifyCodeResetPassword(@PathVariable("email") String email, @RequestBody Map<String, String> body){
        var verify = service.verifyCodeResetPassword(email, body.get("code"));

        if(verify){
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    @PatchMapping("/{email}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable("email") String email, @Valid @RequestBody RequestResetPasswordDTO resetPasswordDTO){
        if(service.verifyCodeResetPassword(email, resetPasswordDTO.getCode())){
            service.resetPassword(email, resetPasswordDTO.getPassword());
            return ResponseEntity.ok("");
        }
        return ResponseEntity.status(401).body("Infelizmente código não correspondente");
    }

    @PostMapping("/create")
    public AccountSummaryDTO create(@Valid @RequestBody CreateAccountDTO account){
        return service.create(account);
    }

    @PutMapping("/{id}/update")
    public AccountSummaryDTO update(Long id,@RequestBody UpdateAccountDTO account){
        return service.update(id, account);
    }

    @GetMapping("/{id}/")
    public AccountSummaryDTO read(Long id){
        return service.read(id);
    }

    @GetMapping("/me")
    public AccountSummaryDTO readMe(){
        return service.read();
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('admin')")
    public Page<AccountSummaryDTO> readAll(Pageable pageable){
        return service.readAllByPageable(pageable);
    }
}
