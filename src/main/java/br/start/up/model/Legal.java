package br.start.up.model;

import br.start.up.enums.LegalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "legals")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Legal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LegalType type;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean mandatory;

    private OffsetDateTime createAt;

    private OffsetDateTime updateAt;
}
