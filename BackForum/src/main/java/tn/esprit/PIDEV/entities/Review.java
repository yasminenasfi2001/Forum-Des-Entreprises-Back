package tn.esprit.PIDEV.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Review implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idReview ;

    private int nbS;

    @ManyToOne
    @JsonIgnore
    private Offre offre;

    @ManyToOne
    private User user;
}
