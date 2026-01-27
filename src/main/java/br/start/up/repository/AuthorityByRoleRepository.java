package br.start.up.repository;

import br.start.up.model.AuthorityByRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityByRoleRepository extends JpaRepository<AuthorityByRole, Long> {

    List<AuthorityByRole> findAllByRole(String role);
}
