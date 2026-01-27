package br.start.up.dtos.summary;

import br.start.up.enums.RiskLevel;
import lombok.Data;

@Data
public class RiskSummaryDTO {

    private Long id;

    private String name;

    private String description;

    private RiskLevel seasonality;

    private String mitigationTip;
}
