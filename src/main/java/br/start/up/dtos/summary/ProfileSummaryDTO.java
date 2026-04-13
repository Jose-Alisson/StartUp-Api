package br.start.up.dtos.summary;

import lombok.Data;

import java.util.List;

@Data
public class ProfileSummaryDTO {

    private String username;

    private String cellphone;

    private String imageUrl;

    private List<String> favoriteBusinessIds;
}
