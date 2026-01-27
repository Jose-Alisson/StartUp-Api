package br.start.up.dtos.summary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountSummaryWithTokenAccessDTO {

    private AccountSummaryDTO account;

    private String token;
}
