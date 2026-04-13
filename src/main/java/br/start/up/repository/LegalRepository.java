package br.start.up.repository;

import br.start.up.model.Legal;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalRepository extends JpaRepository<Legal, Long>, JpaSpecificationExecutor<Legal> {

    @Query(value = """
    SELECT *
    FROM legals
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
    Page<Legal> search(@Param("term") String term, Pageable pageable);

}
