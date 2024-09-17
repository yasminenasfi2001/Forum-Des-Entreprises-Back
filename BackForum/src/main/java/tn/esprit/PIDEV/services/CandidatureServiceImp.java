package tn.esprit.PIDEV.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.esprit.PIDEV.entities.Candidature;
import tn.esprit.PIDEV.entities.Offre;
import tn.esprit.PIDEV.entities.Status;
import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.repositories.CandidatureRepository;
import tn.esprit.PIDEV.repositories.OffreRepository;
import tn.esprit.PIDEV.repositories.UserRepository;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@AllArgsConstructor
public class CandidatureServiceImp implements ICandidatureService {
    CandidatureRepository candidatureRepository;
    OffreRepository offreRepository;
    UserRepository userRepository;

    private final JavaMailSender javaMailSender;
    @Override
    public int getNombreCandidaturesAccepteesByOffreId(Long idOffre) {
        return candidatureRepository.countCandidatureByStatusAndOffreIdOffre(Status.Acceptee, idOffre);
    }
    @Override
    public Candidature addCandidature(Candidature candidature) {
        return candidatureRepository.save(candidature);
    }

    @Override
    public Candidature getCandById(Long idCandidature) {
        return candidatureRepository.findById(idCandidature).orElse(null);
    }

    @Override
    public Candidature updateCandidature(Candidature candidature) {
        return candidatureRepository.save(candidature);
    }

    @Override
    public List<Candidature> getAllCandidature() {
        return candidatureRepository.findAll();
    }

    @Override
    public void deleteCandidature(long idCandidature) {
        candidatureRepository.deleteById(idCandidature);

    }
    @Override
    @Transactional
    public Candidature addCandidatureAndAssignToOfferAndUser (Candidature candidature , Long idOffre, Long idUser,HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {
        Offre offre = offreRepository.findById(idOffre).orElse(null);
        User user = userRepository.findById(idUser).orElse(null);
        if (offre != null && user!=null) {
            candidature.setOffre(offre);
            candidature.setUser(user);
            offre.getCandidaturess().add(candidature);
            user.getCandidatures().add(candidature);
            sendMaildePostulation(candidature,servletRequest);
            offreRepository.save(offre);
            userRepository.save(user);
            //Long ownerId = offre.getUser().getId();



            return candidatureRepository.save(candidature);
        } else {
            return null;
        }
    }

    @Override
    public List<Candidature> getCandidaturesByUserId(Long idUser) {
        return  candidatureRepository.findByUserId(idUser);
    }
    @Override
    public Status getStatusByCandidatureId(Long candidatureId) {
        return candidatureRepository.findById(candidatureId)
                .map(Candidature::getStatus)
                .orElse(null);
    }
    @Override
    public Candidature updateCandidatureSt(Candidature candidature, Status newStatus) {
        if (candidature != null && candidature.getOffre() != null) {
            long idOffre = candidature.getOffre().getIdOffre();
            Offre offre = offreRepository.findById(idOffre).orElse(null);

            if (offre != null) {
                candidature.setOffre(offre);
                candidature.setStatus(newStatus);
                return candidatureRepository.save(candidature);
            }
        }
        return null;
    }

    @Override
    public List<Candidature> getCandidatureByIdOffre(Long idOffre) {
        return candidatureRepository.findByOffreIdOffre(idOffre);
    }

    private void sendMaildePostulation(Candidature candidature, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {

        String subject = "Postulé(e)";
        String senderName = "BUGBUTTLERS";
        String mailContent = "<p> Hi, " + candidature.getUser().getUsername() + ", </p> <br>" +
                "<p><b><br>Vous étes insscrits à la candidature</b>" + candidature.getOffre().getIntitule()+" <br>" +
                "Bon Courage </p> <br>" +

                "<p> Forum des Entreprises d'ESPRIT";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("piemepieme494@gmail.com", senderName);
        messageHelper.setTo(candidature.getUser().getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
        candidatureRepository.save(candidature);

    }

}
