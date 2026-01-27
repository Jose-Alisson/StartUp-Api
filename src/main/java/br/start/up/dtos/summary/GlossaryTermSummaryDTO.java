package br.start.up.dtos.summary;

import jakarta.persistence.Id;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class GlossaryTermSummaryDTO {

    private String id;

    private String name;

    private String imageUrl;
}
