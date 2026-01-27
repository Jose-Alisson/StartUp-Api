package br.start.up.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Table(name = "glossary_term")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GlossaryTerm {

    @Id
    private String id;

    private String name;

    private String imageUrl;

    private OffsetDateTime createAt;

    private OffsetDateTime updateAt;
}
