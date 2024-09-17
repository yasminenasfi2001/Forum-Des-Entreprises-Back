package tn.esprit.PIDEV.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.esprit.PIDEV.entities.Offre;
import tn.esprit.PIDEV.entities.Session;
import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.repositories.OffreRepository;
import tn.esprit.PIDEV.repositories.SessionRepository;
import tn.esprit.PIDEV.repositories.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
@AllArgsConstructor
public class OffreServiceImp implements IOffreService{
    private OffreRepository offreRepository;
    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    @Override
    public Offre addOffre(Offre o) {
        return offreRepository.save(o);
    }

    @Override
    public List<Offre> getAllOffres() {
        return  offreRepository.findAll();
        }


    @Override
    public Offre getOffreById(long idOffre) {return offreRepository.findById(idOffre).orElse(null);}

    @Override
    public void deleteOffre(long idOffre) {offreRepository.deleteById(idOffre);}

    @Override
    public Offre updateOffre(Offre o) {return offreRepository.save(o);}

    @Override
    @Transactional
    public Offre addOffreAndAssignToUserAndToSession(Long idUser, Offre offre, Long idSession) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + idUser));
        Session session = sessionRepository.findById(idSession)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + idSession));
        offre.setUser(user);
        offre.setSessions(session);
        String qrData = "OffreDetails:" + offre.getIdOffre() + ",Titre:" + offre.getIntitule() + ",Description:" + offre.getDescription()+", Nombre de place:"+offre.getNbPlaces()+", Date de création:"+offre.getDateDeCreation()+", Sociéte:"+offre.getUser().getUsername();
        String qrCodeUrl = generateQRCode(qrData);
        offre.setQrcode(qrCodeUrl);
        return offreRepository.save(offre);
    }

    @Override
    public List<Offre> findAllWithReviews() {
        return offreRepository.findAllWithReviews();
    }

    private String generateQRCode(String data) {
        return "http://api.qrserver.com/v1/create-qr-code/?data=" + data + "&size=100x100";
    }
    public List<Offre> getOffresByUserId(Long idUser) {
        return  offreRepository.findByUserId(idUser);
    }

    @PostConstruct
    public void executePythonScript() {
        try {
            String pythonScriptPath = "C:\\Users\\MSII\\Desktop\\4SAE\\Pidevbackspring-main\\Pidevbackspring-main\\src\\main\\resources\\LinkedIn_web_scraper_for_chart.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script executed successfully");
            } else {
                System.out.println("Error executing Python script, exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
