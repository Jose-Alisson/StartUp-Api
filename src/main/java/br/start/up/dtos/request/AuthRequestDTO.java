package br.start.up.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class AuthRequestDTO implements Serializable {

    @NotBlank(message = "O e-email não pode ser vazio")
    @Email(message = "Insira um e-mail valido")
    @NotNull(message = "O e-mail não pode ser nulo")
    private String email;

    @NotBlank(message = "A senha não pode ser vazia")
    @NotNull(message = "A senha não pode ser nula")
    private String password;
}
