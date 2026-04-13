package br.start.up.dtos.request;


import br.start.up.enums.LegalStructureType;
import br.start.up.enums.ValidEnum;
import br.start.up.model.Legal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LegalStructureRequestDTO {

    private Long id;

    @ValidEnum(enumClass = LegalStructureType.class, message = "MEI, ME")
    @NotNull(message = "O campo não pode ser nulo")
    private String type;

    @NotNull(message = "O campo não pode ser nulo")
    private List<LegalRequestDTO> requirements;
}
