package tn.esprit.PIDEV.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PIDEV.entities.Feedback;
import tn.esprit.PIDEV.services.IFeedbackService;

import java.util.List;

@RequestMapping("/api/auth")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
public class FeedbackController {
    private IFeedbackService iFeedbackService;



    @PostMapping("/addFeedbackAndAssignFeedbackToUser/{idUser}")
    public Feedback addFeedbackAndAssignFeedbackToUser(@RequestBody Feedback f, @PathVariable long idUser) {
        return iFeedbackService.addFeedbackAndAssignFeedbackToUser(idUser, f);
    }

    @GetMapping("/getAllF")
    public List<Feedback> getAllFeedback(){
        return iFeedbackService.getAllFeedback();
    }


}
