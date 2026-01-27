package br.start.up.services;

import br.start.up.model.Account;
import br.start.up.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private String PREFIX = "metrics:signup";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RedisTemplate<String, Object> redis;

    public void countNewUser(){

    }

    @Scheduled(cron = "0 5 0 * * *")
    public void calculateMetrics(){
        var currentMonth = LocalDate.now(ZoneId.of("America/Sao_paulo"));
        var lastedMonth = currentMonth.minusMonths(1);

        var patternFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        var accounts = accountRepository.findAllByDateBetween(
                lastedMonth.format(patternFormat),
                currentMonth.format(patternFormat)
                );

        var newUsersByWeek = accounts.stream().map(a -> LocalDate.from(a.getCreateAt()))
                .collect(Collectors.groupingBy(d -> {
                    int week = (d.getDayOfMonth() - 1) / 7 + 1;
                    return "W" + week;
                }));
    }

    public void getMetrics(){

    }
}
