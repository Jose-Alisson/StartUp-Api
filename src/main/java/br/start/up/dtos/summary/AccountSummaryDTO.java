package br.start.up.dtos.summary;

import lombok.Data;

@Data
public class AccountSummaryDTO {

    private Long id;

    private ProfileSummaryDTO profile;

    private String username;

    private String email;

    private String role;
}
