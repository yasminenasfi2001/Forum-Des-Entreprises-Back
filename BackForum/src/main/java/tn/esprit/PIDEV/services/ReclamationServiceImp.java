package tn.esprit.PIDEV.services;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tn.esprit.PIDEV.entities.Reclamation;
import tn.esprit.PIDEV.entities.Status;
import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.repositories.ReclamationRepository;
import tn.esprit.PIDEV.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReclamationServiceImp implements IReclamationService {
    private UserRepository userRepository;

    private ReclamationRepository reclamationReposiory;

    public Reclamation addReclamation(Reclamation r) {
        return reclamationReposiory.save(r);
    }

    @Override
    public List<Reclamation> getAllReclamations() {
        return reclamationReposiory.findAll();
    }

    @Override
    public List<Reclamation> getReclamationsByUserId(Long id) {
        return reclamationReposiory.findByUserId(id);
    }

    @Override
    public Reclamation getReclamationById(long idReclamation) {
        return reclamationReposiory.findById(idReclamation).orElse(null);
    }



    @Override
    public void deleteReclamation(long idReclamation) {
        reclamationReposiory.deleteById(idReclamation);
    }

    @Override
    public Reclamation updateReclamation(Reclamation r) {
        return reclamationReposiory.save(r);
    }

    @Override
    public Reclamation addReclamationAndAssignReclamationToUser(long idUser, Reclamation reclamation) {
        User user = userRepository.findById(idUser).orElse(null);
        if (user != null) {
            reclamation.setUser(user);
            userRepository.save(user);
            return reclamationReposiory.save(reclamation);
        } else {
            return null;
        }
    }

}
