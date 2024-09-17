package tn.esprit.PIDEV.controllers;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PIDEV.entities.Materiel;
import tn.esprit.PIDEV.services.IMaterielService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/materiels")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
public class MaterielController {

    private IMaterielService materielService;

    @PostMapping("add")
    public ResponseEntity<Materiel> addMateriel(@RequestBody Materiel materiel) {
        Materiel addedMateriel = materielService.addMateriel(materiel);
        return new ResponseEntity<>(addedMateriel, HttpStatus.CREATED);
    }

    @GetMapping("getAll")
    public ResponseEntity<List<Materiel>> getAllMateriels() {
        List<Materiel> materiels = materielService.retrieveAllMateriel();
        return new ResponseEntity<>(materiels, HttpStatus.OK);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<Materiel> getMaterielById(@PathVariable Long id) {
        Materiel materiel = materielService.retrieveMateriel(id);
        return new ResponseEntity<>(materiel, HttpStatus.OK);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Materiel> updateMateriel(@PathVariable Long id, @RequestBody Materiel materiel) {
        materiel.setIdMateriel(id);
        Materiel updatedMateriel = materielService.updateMateriel(materiel);
        return new ResponseEntity<>(updatedMateriel, HttpStatus.OK);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteMateriel(@PathVariable Long id) {
        materielService.deleteMateriel(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

