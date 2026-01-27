package br.start.up.repository;

import br.start.up.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByEmail(String email);

    @Query("from Account a where a.email = :email")
    Optional<Account> findByEmailOptional(@Param("email") String email);

    @Query(value = "select * from accounts where date(create_at) between ?1 and ?2", nativeQuery = true)
    List<Account> findAllByDateBetween(String start, String end);

}
