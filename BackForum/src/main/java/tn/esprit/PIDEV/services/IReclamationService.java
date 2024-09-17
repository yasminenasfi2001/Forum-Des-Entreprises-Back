package tn.esprit.PIDEV.services;

import  org.springframework.http.ResponseEntity;
import tn.esprit.PIDEV.entities.Reclamation;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.PIDEV.entities.Status;

import java.io.IOException;
import java.util.List;

public interface IReclamationService {
    Reclamation addReclamation(Reclamation r) ;
    public List<Reclamation> getAllReclamations();
    public List<Reclamation> getReclamationsByUserId(Long id);




    public Reclamation getReclamationById(long idReclamation);
    public void deleteReclamation(long idReclamation);
    public Reclamation updateReclamation(Reclamation r);
    public Reclamation addReclamationAndAssignReclamationToUser (long idUser , Reclamation reclamation);

}