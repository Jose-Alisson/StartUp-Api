package br.start.up.model;

import br.start.up.enums.LegalStructureType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "legal_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LegalStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LegalStructureType type;

    @ManyToMany
    @JoinTable(name = "l_structure_legals", joinColumns = @JoinColumn(name = "structure_id"), inverseJoinColumns = @JoinColumn(name = "legal_id"))
    private List<Legal> requirements;
}
