package tn.esprit.PIDEV.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.PIDEV.entities.Feedback;
import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.repositories.CandidatureRepository;
import tn.esprit.PIDEV.repositories.FeedbackRepository;
import tn.esprit.PIDEV.repositories.OffreRepository;
import tn.esprit.PIDEV.repositories.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class FeedbackServiceImp implements IFeedbackService{

    FeedbackRepository feedbackRepository;
    UserRepository userRepository;
    @Override
    public Feedback addFeedbackAndAssignFeedbackToUser(long idUser, Feedback f) {
        User user = userRepository.findById(idUser).orElse(null);
        if (user != null) {
            f.setUser(user);
            userRepository.save(user);
            return feedbackRepository.save(f);
        } else {
            return null;
        }

    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
}
