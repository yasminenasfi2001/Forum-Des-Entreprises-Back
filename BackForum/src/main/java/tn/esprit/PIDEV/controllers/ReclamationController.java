package tn.esprit.PIDEV.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PIDEV.entities.Reclamation;
import tn.esprit.PIDEV.services.IReclamationService;

import java.util.List;

@RequestMapping("/api/auth")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
public class ReclamationController {
    private IReclamationService iReclamationService;



    @PostMapping("/addReclamation")
    public Reclamation addReclamation(
            @RequestBody Reclamation r){
        return iReclamationService.addReclamation(r);
    }

    @GetMapping("/getAllr")
    public List<Reclamation> getAllReclamation(){
        return iReclamationService.getAllReclamations();
    }

    @PutMapping("/modifierReclamation")
    public Reclamation updateReclamation(@RequestBody Reclamation r) {
        return iReclamationService.updateReclamation(r);
    }

    @GetMapping("/getReclamation/{idReclamation}")
    public Reclamation findById(@PathVariable long idReclamation) {
        return iReclamationService.getReclamationById(idReclamation);
    }

    @DeleteMapping("/supprimerReclamation/{idReclamation}")
    public void deleteReclamation(@PathVariable long idReclamation) {
        iReclamationService.deleteReclamation(idReclamation);
    }

    @PostMapping("/addReclamationAndAssignReclamationToUser/{idUser}" )
    public Reclamation addReclamationAndAssignRecalamtionToUser (@RequestBody Reclamation r, @PathVariable long idUser) {
        return iReclamationService.addReclamationAndAssignReclamationToUser(idUser , r);
    }
    @GetMapping("/getMyRecalamations/{id}")
    public List<Reclamation> getReclamationsByUserId(@PathVariable Long id){return iReclamationService.getReclamationsByUserId(id);}

}
