package br.start.up.repository;

import br.start.up.dtos.summary.CategorySummaryDTO;
import br.start.up.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query("FROM Category WHERE name = :name")
    Category findByName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE categories SET affiliations_count = COALESCE(affiliations_count, 0) + 1 WHERE id = ?1", nativeQuery = true)
    void incrementAffiliation(Long id);

    @Query(value = """
    SELECT *
    FROM categories
    WHERE 
        to_tsvector('portuguese', name) @@ plainto_tsquery('portuguese', :term)
        OR name ILIKE CONCAT('%', :term, '%')
""",
            countQuery = """
    SELECT COUNT(*)
    FROM categories
    WHERE 
        to_tsvector('portuguese', name) @@ plainto_tsquery('portuguese', :term)
        OR name ILIKE CONCAT('%', :term, '%')
""",
            nativeQuery = true)
    Page<Category> search(@Param("term") String term, Pageable pageable);
}
