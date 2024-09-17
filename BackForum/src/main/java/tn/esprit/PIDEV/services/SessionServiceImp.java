package tn.esprit.PIDEV.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.PIDEV.entities.Materiel;
import tn.esprit.PIDEV.entities.Pack;
import tn.esprit.PIDEV.entities.Session;
import tn.esprit.PIDEV.repositories.MaterielRepository;
import tn.esprit.PIDEV.repositories.SessionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class SessionServiceImp implements  ISessionService {


    EmailService emailService;
    SessionRepository sessionRepository;
    MaterielRepository materielRepository;
    @Override
    public Session addSession(Session s) {
        String to="anisfarjallah120@outlook.com";
        String subject="Session ajoutée";
        String body="Session ajoutée avec succés";
        emailService.sendSimpleMessage(to,subject,body);
        return sessionRepository.save(s);
    }
    @Override
    public Session retrieveSession(Long id) {
        return sessionRepository.findByIdSession(id).orElseThrow(() -> new IllegalArgumentException("no instructor found with this id " + id));
    }

    @Override
    public void removeSession(Long idSession) {
        sessionRepository.deleteById(idSession);

    }

    @Override
    public Session modifiySession(Long idSession, Session updateSession) {
        Optional<Session> optUpdate = sessionRepository.findByIdSession(idSession);
        if (optUpdate.isPresent()){
            Session sessioExist = optUpdate.get();
            sessioExist.setIntitule(updateSession.getIntitule());
            sessioExist.setDateSession(updateSession.getDateSession());
            sessioExist.setNbrDePlaces(updateSession.getNbrDePlaces());

            return sessionRepository.save(sessioExist);

        }else {
            return  null;
        }

    }

    @Override
    public List<Session> retrieveAllSession() {
        List<Session> sessionList = sessionRepository.findAll();
        for(Session S:sessionList){
            log.info("Session"+S);
        }
        return sessionList;
    }
    @Override
    public void assignMaterialToSession(Long sessionId, Pack pack) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("No session found with this id: " + sessionId));

        List<Materiel> materiels = materielRepository.findByPack(pack);

        for (Materiel materiel : materiels) {
            materiel.setSessions(session);
        }

        materielRepository.saveAll(materiels);
    }
}

