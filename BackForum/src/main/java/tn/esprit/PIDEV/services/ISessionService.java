package tn.esprit.PIDEV.services;

import tn.esprit.PIDEV.entities.Pack;
import tn.esprit.PIDEV.entities.Session;

import java.util.List;

public interface ISessionService{
    Session addSession(Session s);

    public void removeSession(Long idSession);

    public Session modifiySession(Long idSession, Session updateSession);

    public List<Session> retrieveAllSession();
    public Session retrieveSession(Long id);
    public void assignMaterialToSession(Long sessionId, Pack pack);
}
