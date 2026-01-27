package br.start.up.repository;

import br.start.up.model.GlossaryTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GlossaryTermRepository extends JpaRepository<GlossaryTerm, String>, JpaSpecificationExecutor<GlossaryTerm> {
}
