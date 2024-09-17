package tn.esprit.PIDEV.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.PIDEV.entities.Candidature;
import tn.esprit.PIDEV.entities.Status;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByUserId(Long idUser);
    List<Candidature> findByOffreIdOffre(Long idOffre);

    int countCandidatureByStatusAndOffreIdOffre(Status status, long idOffre);

}
