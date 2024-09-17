package tn.esprit.PIDEV.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.PIDEV.entities.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
