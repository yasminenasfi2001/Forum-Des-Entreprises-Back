package tn.esprit.PIDEV.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PIDEV.entities.Candidature;
import tn.esprit.PIDEV.entities.Status;
import tn.esprit.PIDEV.services.ICandidatureService;

import java.io.UnsupportedEncodingException;
import java.util.List;

@AllArgsConstructor

@RestController
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowCredentials="true")
@RequestMapping("/api/auth")
public class CandidatureController {
    ICandidatureService iCandidatureService;

    @GetMapping("/getAllC")
    public List<Candidature> getAllCandidature() {
        return iCandidatureService.getAllCandidature();
    }

    @PostMapping(value = "/addCandidature")
    public Candidature addCandidature(@RequestBody Candidature candidature) {
        return iCandidatureService.addCandidature(candidature);
    }

    @DeleteMapping("/supprimerCandidature/{idCandidature}")
    public void deleteCandidature(@PathVariable long idCandidature) {
        iCandidatureService.deleteCandidature(idCandidature);
    }

    @PostMapping(value = "/addCandidatureAndAssignToOffer/{idUser}/{idOffre}")
    public Candidature addCandidatureAndAssignToOffer(@PathVariable Long idUser, @PathVariable Long idOffre, @RequestBody Candidature candidature, HttpServletRequest servletRequest)
        throws MessagingException, UnsupportedEncodingException {
            return iCandidatureService.addCandidatureAndAssignToOfferAndUser(candidature, idOffre, idUser, servletRequest);
        }

    @GetMapping(value = "/getCandidaturesByUserId/{idUser}")
    public List<Candidature> getCandidaturesByUser(@PathVariable Long idUser) {
        return iCandidatureService.getCandidaturesByUserId(idUser);
    }
    @GetMapping(value = "/getCandidaturesById/{idCandidaturer}")
    public Candidature getCandidaturesById(@PathVariable Long idCandidaturer) {
        return iCandidatureService.getCandById(idCandidaturer);
    }

    @PutMapping("/modifierCandidature")
    public Candidature updateCandidature(@RequestBody Candidature candidature ) {
        return iCandidatureService.updateCandidature(candidature);
    }

    @PutMapping("/modifierCandidatureaaa/{stt}")
    public Candidature updateCandidatureStat(@RequestBody Candidature candidature,@PathVariable Status stt ) {
        return iCandidatureService.updateCandidatureSt(candidature,stt);
    }

    @GetMapping(value="/getStatusByCandidId/{candidatureId}")
    public Status getStatusByCandidatureId (@PathVariable Long candidatureId){
        return  iCandidatureService.getStatusByCandidatureId(candidatureId);
    }
    @GetMapping(value="/CandidatureByIdOffre/{idOffre}")
    public List<Candidature> getCandidatureByIdOffre (@PathVariable Long idOffre){
        return  iCandidatureService.getCandidatureByIdOffre(idOffre);
    }
    @GetMapping(value="/getNombreCandidaturesAcceptees/{idOffre}")
    public int getNombreCandidaturesAccepteesByOffreId(@PathVariable Long idOffre) {
        return iCandidatureService.getNombreCandidaturesAccepteesByOffreId(idOffre);
    }
}