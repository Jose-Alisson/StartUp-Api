package br.start.up.repository;

import br.start.up.model.Legal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalRepository extends JpaRepository<Legal, Long>, JpaSpecificationExecutor<Legal> {
}
