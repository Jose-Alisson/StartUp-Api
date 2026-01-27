package br.start.up.dtos.request;

import br.start.up.enums.RiskLevel;
import br.start.up.enums.ValidEnum;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RicksRequestDTO {

    private Long id;

    @NotNull(message = "O campo não pode ser nulo")
    @NotBlank(message = "O campo não pode ser vazio")
    private String name;

    private String description;

    @ValidEnum(enumClass = RiskLevel.class, message = "LOW, MEDIUM, HIGH")
    @NotNull(message = "O campo não pode ser nulo")
    private String seasonality;

    private String mitigationTip;
}
