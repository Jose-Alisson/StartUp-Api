package br.start.up.dtos.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountDTO {
    @NotBlank(message = "O nome de usuário não pode ser vazio")
    @NotNull(message = "O nome de usuário não pode ser nulo")
    @Length(min = 4, max = 120, message = "O nome deve ter no minimo 4 caracteres")
    private String username;

    @NotBlank(message = "O e-email não pode ser vazio")
    @NotNull(message = "O e-mail não pode ser nulo")
    @Email(message = "Insira um e-mail valido")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "A senha não pode ser vazia")
    @Length(message = "A senha deve ter entre 8 a 16 caracteres", min = 8, max = 16)
    @NotNull(message = "A senha não pode ser nula")
    private String password;
}
