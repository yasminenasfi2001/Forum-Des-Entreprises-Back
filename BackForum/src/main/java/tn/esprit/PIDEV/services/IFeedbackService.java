package tn.esprit.PIDEV.services;

import tn.esprit.PIDEV.entities.Feedback;

import java.util.List;

public interface IFeedbackService {
    public Feedback addFeedbackAndAssignFeedbackToUser (long idUser , Feedback f);

    public List<Feedback> getAllFeedback();
}
