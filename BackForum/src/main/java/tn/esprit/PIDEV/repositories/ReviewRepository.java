package tn.esprit.PIDEV.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.PIDEV.entities.Review;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Query("SELECT r FROM Review r WHERE r.offre.idOffre = :idOffer AND r.user.id = :idUser")
    Review findByOfferIdAndUserId(@Param("idOffer") Long idOffer, @Param("idUser") Long idUser);
}
