package br.start.up.repository;

import br.start.up.dtos.summary.BusinessSummaryDTO;
import br.start.up.model.Business;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long>, JpaSpecificationExecutor<Business> {

    @Transactional
    @Query(value = "update businesses set is_deleted = ?2 where name = ?1 or cast(id as varchar) = ?1", nativeQuery = true)
    @Modifying
    void setDeleted(String id, boolean value);

    @Query(value = "select * from businesses where name = ?1 or cast(id as varchar) = ?1", nativeQuery = true)
    Optional<Business> findByIdOrName(String idOrName);

    @Query(value = "select exists(select 1 from businesses where name = ?1 or cast(id as varchar) = ?1)", nativeQuery = true)
    boolean existByIdOrName(String id);

    List<Business> findByIsFeaturedTrue();

    @Modifying
    @Query("UPDATE Business b SET b.isFeatured = false WHERE b.isFeatured = true AND b.id NOT IN :ids")
    void removeFeaturedOutsideTop(Set<Long> ids);

    @Modifying
    @Query("UPDATE Business b SET b.isFeatured = true WHERE b.id in :ids")
    void markAsFeatured(Set<Long> ids);


    @Query("SELECT u FROM Business u WHERE u.id IN :ids")
    Page<Business> findAllById(@Param("ids") List<Long> ids, Pageable pageable);
}
