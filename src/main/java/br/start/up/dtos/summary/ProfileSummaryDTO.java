package br.start.up.dtos.summary;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinTable;
import lombok.Data;

import java.util.List;

@Data
public class ProfileSummaryDTO {

    private String username;

    private List<String> favoriteBusinessIds;
}
