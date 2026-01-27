package br.start.up.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Table(name = "categories")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageUrl;

    private OffsetDateTime createAt;

    private OffsetDateTime updateAt;
}
