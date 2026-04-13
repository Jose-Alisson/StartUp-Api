package br.start.up.dtos.request;

import br.start.up.dtos.summary.LegalStructureSummaryDTO;
import br.start.up.dtos.summary.RiskSummaryDTO;
import br.start.up.enums.RiskLevel;
import br.start.up.model.Category;
import br.start.up.model.CheckListItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class BusinessRequestDTO implements Serializable {

    @NotNull(message = "O campo não pode ser nulo")
    @NotBlank(message = "O campo não pode ser vazio")
    private String name;

    @NotNull(message = "O campo não pode ser nulo")
    @NotBlank(message = "O campo não pode ser vazio")
    private String category;

    private String description;

    private String imageUrl;

    private BigDecimal initialInvestment;

    private BigDecimal monthlyProfit;

    private BigDecimal profitMargin;

    private boolean isFeatured;

    @Valid
    @NotNull(message = "O campo não pode ser nulo")
    private List<RicksRequestDTO> ricks;

    @Valid
    @NotNull(message = "O campo não pode ser nulo")
    private LegalStructureRequestDTO legalStructure;

    @Valid
    @NotNull(message = "O campo não pode ser nulo")
    private List<CheckListItemRequestDTO> checkList;

    @NotNull(message = "O campo não pode ser nulo")
    private Set<String> tips;

}
