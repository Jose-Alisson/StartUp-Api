package br.start.up.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessWithIdRequestDTO {
    private Long id;

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
