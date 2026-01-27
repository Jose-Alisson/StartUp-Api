package br.start.up.dtos.summary;

import lombok.Data;

@Data
public class LegalSummaryDTO {

    private Long id;

    private String type;

    private String name;

    private String description;

    private boolean mandatory;
}
