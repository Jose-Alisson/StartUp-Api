package br.start.up.dtos.request;

import br.start.up.enums.LegalType;
import br.start.up.enums.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LegalRequestDTO {

    @ValidEnum(enumClass = LegalType.class, message = "DOCUMENT, LICENSE, TAX")
    @NotNull(message = "O campo não pode ser nulo")
    private String type;

    @NotNull(message = "O campo não pode ser nulo")
    @NotBlank(message = "O campo não pode ser vazio")
    private String name;

    private String description;

    private boolean mandatory;
}
