package br.start.up.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResetPasswordDTO {

    @NotBlank(message = "A senha não pode ser vazia")
    @NotNull(message = "A senha não pode ser nula")
    @Length(message = "A senha deve ter entre 8 a 16 caracteres", min = 8, max = 16)
    private String password;

    @NotBlank(message = "A código não pode ser vazio")
    @NotNull(message = "A código não pode ser nulo")
    @Length(min = 6, max = 6, message = "O código deve conter 6 caracteres")
    private String code;
}
