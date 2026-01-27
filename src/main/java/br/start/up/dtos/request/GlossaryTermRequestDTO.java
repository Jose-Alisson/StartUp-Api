package br.start.up.dtos.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GlossaryTermRequestDTO {

    @NotNull(message = "O campo não pode ser nulo")
    @NotBlank(message = "O campo não pode ser vazio")
    private String name;

    private String imageUrl;
}
