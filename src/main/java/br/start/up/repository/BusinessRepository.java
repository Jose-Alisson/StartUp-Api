package br.start.up.repository;

import br.start.up.model.Business;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long>, JpaSpecificationExecutor<Business> {

    @Transactional
    @Query(value = "update from Business b set b.is_deleted = ?2 where name = ?1 or cast(b.id as varchar) = ?1", nativeQuery = true)
    @Modifying
    void setDeleted(String id, boolean value);

    @Query(value = "select * from businesses where name = ?1 or cast(id as varchar) = ?1", nativeQuery = true)
    Optional<Business> findByIdOrName(String idOrName);

    @Query(value = "select exists(select 1 where name = ?1 or cast(id as varchar) = ?1)", nativeQuery = true)
    boolean existByIdOrName(String id);
}
