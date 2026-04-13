package br.start.up.dtos.summary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class BusinessSummaryDTO {

    private Long id;

    private String name;

    private CategorySummaryDTO category;

    private String description;

    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal initialInvestment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal monthlyProfit;

    private BigDecimal profitMargin;

    private boolean isFeatured;

    private List<RiskSummaryDTO> ricks;

    private LegalStructureSummaryDTO legalStructure;

    private List<CheckListItemSummaryDTO> checkList;

    private Set<String> tips;

    private JsonNode moreData;

    private boolean visible;

    private boolean isDeleted;

    private BusinessInflation inflation;
}
