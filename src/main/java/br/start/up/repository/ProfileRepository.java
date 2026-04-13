package br.start.up.repository;

import br.start.up.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query(value = "SELECT p.* FROM profiles p INNER JOIN accounts a on a.profile_id = p.id and a.email = ?1", nativeQuery = true)
    Optional<Profile> findProfileByEmail(String email);

    @Modifying
    @Query(value = "UPDATE profiles p SET image_url = ?1 FROM accounts a where a.profile_id = p.id and a.email = ?2", nativeQuery = true)
    void updateImageUrl(String imageUrl, String principal);
}
