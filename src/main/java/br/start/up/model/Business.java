package br.start.up.model;

import br.start.up.enums.RiskLevel;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;


@Table(name = "businesses")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private BigDecimal initialInvestment;

    private BigDecimal monthlyProfit;

    private BigDecimal profitMargin;

    private boolean isFeatured;

    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "business_id")
    private List<Risk> ricks;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "legal_structure_id")
    private LegalStructure legalStructure;

    @OneToMany(cascade = {PERSIST, MERGE, DETACH})
    @JoinTable(name = "business_check_list_items")
    private List<CheckListItem> checkList;

    @ElementCollection
    @CollectionTable(name = "business_tips")
    @Column(name = "tip")
    private Set<String> tips;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode moreData;

    private boolean visible;

    private boolean isDeleted;

    private OffsetDateTime createAt;

    private OffsetDateTime updateAt;
}
