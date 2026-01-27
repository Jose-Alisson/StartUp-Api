package br.start.up.dtos.summary;

import br.start.up.enums.CheckListType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CheckListItemSummaryDTO {

    private Long id;

    private String title;

    private String description;

    private CheckListType type;

    private boolean isCompleted;
}
