package tn.esprit.PIDEV.services;

import tn.esprit.PIDEV.entities.Offre;
import tn.esprit.PIDEV.entities.Reclamation;
import tn.esprit.PIDEV.entities.Review;

public interface IReviewService {
    public Review addReviewAndAssignToUserAndToOffre(Long idUser, Review review, Long idOffre);
    Review findReviewByOfferIdAndUserId(Long idOffer, Long idUser);

}
