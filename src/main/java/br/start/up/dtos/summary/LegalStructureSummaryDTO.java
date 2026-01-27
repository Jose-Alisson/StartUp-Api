package br.start.up.dtos.summary;

import br.start.up.enums.LegalStructureType;

import lombok.Data;

import java.util.List;

@Data
public class LegalStructureSummaryDTO {

    private Long id;

    private LegalStructureType type;

    private List<LegalSummaryDTO> requirements;
}
