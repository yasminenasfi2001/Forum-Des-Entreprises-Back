package tn.esprit.PIDEV.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Candidature implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCandidature ;

    private String  lettreDeMotivation;
    private String experience;
    private String formation;
    private String competence;
    private String langues;

    private LocalDateTime dateDeCandidature;
    private LocalDateTime dateDeMiseEnTrt;
    private LocalDateTime datedeRefus;
    private LocalDateTime dateDAcceptance;
    private String remarque;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private User user;

    @ManyToOne
    private Offre offre;
}
