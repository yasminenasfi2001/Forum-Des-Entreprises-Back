package tn.esprit.PIDEV.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import tn.esprit.PIDEV.entities.Candidature;
import tn.esprit.PIDEV.entities.Status;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface ICandidatureService {
    public Candidature addCandidature(Candidature candidature);
    public Candidature getCandById(Long idCandidature);
    public Candidature updateCandidature(Candidature candidature);
    public List<Candidature> getAllCandidature();
    public void deleteCandidature(long idCandidature);
    public Candidature addCandidatureAndAssignToOfferAndUser (Candidature candidature , Long idOffre, Long idUser, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException;
    public List<Candidature> getCandidaturesByUserId(Long idUser);
    public Status getStatusByCandidatureId(Long candidatureId);
    public Candidature updateCandidatureSt(Candidature candidature,Status newStatus);
    public List<Candidature>getCandidatureByIdOffre(Long idOffre);
    public int getNombreCandidaturesAccepteesByOffreId(Long idOffre);

}

