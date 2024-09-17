package tn.esprit.PIDEV.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.PIDEV.entities.AuthenticateResponse;
import tn.esprit.PIDEV.entities.User;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {

    public void createPasswordResetTokenUser(User user , String token){

        AuthenticateResponse response = new AuthenticateResponse();
    }
}
