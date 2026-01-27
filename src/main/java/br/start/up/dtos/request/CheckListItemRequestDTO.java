package br.start.up.dtos.request;

import br.start.up.enums.CheckListType;

import br.start.up.enums.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckListItemRequestDTO {

    private Long id;

    @NotNull(message = "O campo não pode ser nulo")
    @NotBlank(message = "O campo não pode ser vazio")
    private String title;

    private String description;

    @ValidEnum(enumClass = CheckListType.class, message = "EQUIPMENT, TEAM, LOCATION, LEGAL")
    @NotNull(message = "O campo não pode ser nulo")
    private String type;

    private boolean isCompleted;
}
