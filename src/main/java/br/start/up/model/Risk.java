package br.start.up.model;

import br.start.up.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ricks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private RiskLevel seasonality;

    @Column(columnDefinition = "TEXT")
    private String mitigationTip;
}
