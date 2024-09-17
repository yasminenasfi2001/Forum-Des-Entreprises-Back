package tn.esprit.PIDEV.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.PIDEV.entities.Materiel;
import tn.esprit.PIDEV.entities.Pack;

import java.util.List;

public interface MaterielRepository extends JpaRepository<Materiel,Long> {

    List<Materiel> findByPack(Pack pack);
}