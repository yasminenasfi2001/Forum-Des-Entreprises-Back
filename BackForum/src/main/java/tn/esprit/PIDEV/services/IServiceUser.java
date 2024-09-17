package tn.esprit.PIDEV.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.PIDEV.entities.ERole;
import tn.esprit.PIDEV.entities.User;

import java.io.IOException;
import java.util.List;

public interface IServiceUser {

    String saveImageForUsers(MultipartFile file) throws IOException;


    Boolean blockUser(Long id);

    List<User> getAllUserWithRole(ERole role);

    boolean createPasswordResetToken(String token , UserDetails userDetails);

    String generateToken(User user);

    void changePassword(User user , String newPassword);
}
