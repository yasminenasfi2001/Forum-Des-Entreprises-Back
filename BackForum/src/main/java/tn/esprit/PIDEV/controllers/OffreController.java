package tn.esprit.PIDEV.controllers;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PIDEV.entities.Offre;
import tn.esprit.PIDEV.services.IOffreService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
public class OffreController {

    private IOffreService iOffreService;




    @PostMapping("/addOffre")
    public Offre addOffre(@RequestBody Offre o){
        return iOffreService.addOffre(o);
    }

    @GetMapping("/getAllO")
    public List<Offre> getAllOffre() {
        return iOffreService.getAllOffres();
    }

    @GetMapping("/getAllaa")
    public List<Offre> getAllOffre1() {
        return iOffreService.findAllWithReviews();
    }

    @PutMapping("/modifierOffre")
    public Offre updateOffre(@RequestBody Offre o){return iOffreService.updateOffre(o);}

    @GetMapping("/getOffre/{idOffre}")
    public Offre findById(@PathVariable long idOffre){
        return iOffreService.getOffreById(idOffre);
    }

    @DeleteMapping("/supprimerOffre/{idOffre}")
    public void deleteOffre(@PathVariable long idOffre){
        iOffreService.deleteOffre(idOffre);
    }

    @GetMapping("/getMyOffres/{id}")
    public List<Offre> getOffresByUserId(@PathVariable Long id){return iOffreService.getOffresByUserId(id);}

    @PostMapping("/addOffreAndAssignToUserAndToSession/{idUser}/{idSession}")
    public Offre addOffreAndAssignToUserAndToSession(@PathVariable Long idUser,@RequestBody Offre o,@PathVariable Long idSession){
        return iOffreService.addOffreAndAssignToUserAndToSession(idUser,o,idSession);
    }

}
