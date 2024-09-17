package tn.esprit.PIDEV.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
public class Session implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idSession ;

    private String intitule;

    private LocalDate dateSession;

    private int nbrDePlaces;


    @ManyToMany(mappedBy = "sessions",cascade = CascadeType.ALL)
    private Set<User> users;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sessions")
    private Set<Materiel> materiels;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sessions")
    private Set<Offre> offres;


}
