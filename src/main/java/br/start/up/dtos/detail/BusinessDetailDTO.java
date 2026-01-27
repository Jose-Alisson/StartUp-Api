package br.start.up.dtos.detail;

import br.start.up.dtos.request.RicksRequestDTO;
import br.start.up.enums.RiskLevel;
import br.start.up.model.Category;
import br.start.up.model.CheckListItem;
import br.start.up.model.LegalStructure;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Data
public class BusinessDetailDTO {

    private Long id;

    private String name;

    private Category category;

    private String description;

    private String imageUrl;

    private BigDecimal initialInvestment;

    private BigDecimal monthlyProfit;

    private BigDecimal profitMargin;

    private boolean isFeatured;

    private List<RicksRequestDTO> ricks;

    private LegalStructure legalStructure;

    private List<CheckListItem> checkList;

    private Set<String> tips;

    private JsonNode moreData;

    private boolean visible;

    private boolean isDeleted;

    private OffsetDateTime createAt;

    private OffsetDateTime updateAt;
}
