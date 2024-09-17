package tn.esprit.PIDEV.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.PIDEV.entities.Offre;
import tn.esprit.PIDEV.entities.Review;
import tn.esprit.PIDEV.entities.Session;
import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.repositories.OffreRepository;
import tn.esprit.PIDEV.repositories.ReviewRepository;
import tn.esprit.PIDEV.repositories.UserRepository;

@Service
@AllArgsConstructor
public class ReviewServiceImp implements IReviewService{
    ReviewRepository reviewRepository;
    UserRepository userRepository;
    OffreRepository offreRepository;
    @Override
    @Transactional
    public Review addReviewAndAssignToUserAndToOffre(Long idUser, Review review, Long idOffre) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + idUser));
        Offre offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new RuntimeException("Offre not found with id: " + idOffre));
        review.setUser(user);
        review.setOffre(offre);
        return reviewRepository.save(review);
    }

    public Review findReviewByOfferIdAndUserId(Long idOffer, Long idUser) {
        return reviewRepository.findByOfferIdAndUserId(idOffer, idUser);
    }

}
