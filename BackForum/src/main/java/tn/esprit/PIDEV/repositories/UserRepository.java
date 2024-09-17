package tn.esprit.PIDEV.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.PIDEV.entities.ERole;
import tn.esprit.PIDEV.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    User findByUsernameLike(String username);
    Boolean existsByEmail(String email);

    List<User> findByRole(ERole roleName);

    Optional<Object> findByEmail(String email);

    Optional<User> findByTel(String tel);
}
