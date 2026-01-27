package br.start.up.model;

import br.start.up.enums.CheckListType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Table(name = "check_list_items")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class CheckListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private CheckListType type;

    @Transient
    private boolean isCompleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updateAt;
}
