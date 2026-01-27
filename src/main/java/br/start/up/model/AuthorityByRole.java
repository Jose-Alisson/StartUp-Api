package br.start.up.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "authority_by_role")
@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class AuthorityByRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    private String role;

    private String authority;
}
