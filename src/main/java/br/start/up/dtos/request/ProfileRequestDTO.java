package br.start.up.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileRequestDTO {

    @NotBlank(message = "O nome não pode ser vazio")
    @NotNull(message = "O nome não pode ser nulo")
    private String username;

    @NotBlank(message = "O telefone não pode ser vazio")
    @NotNull(message = "O telefone não pode ser nulo")
    private String cellphone;
}
