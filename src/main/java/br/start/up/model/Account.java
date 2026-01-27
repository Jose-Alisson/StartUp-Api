package br.start.up.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    @ElementCollection
    @CollectionTable(
            name = "account_authorities",
            joinColumns = @JoinColumn(name = "account_id")
    )
    @Column(name = "authority")
    private Set<String> authorities;

    private boolean active;

    private OffsetDateTime createAt;

    private OffsetDateTime updateAt;
}
