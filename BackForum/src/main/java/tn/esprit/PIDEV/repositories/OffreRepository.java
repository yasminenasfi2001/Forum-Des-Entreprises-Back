package tn.esprit.PIDEV.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.PIDEV.entities.Offre;

import java.util.List;

public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByUserId(Long id);
    @Query("SELECT DISTINCT o FROM Offre o LEFT JOIN FETCH o.reviews")
    List<Offre> findAllWithReviews();
}
