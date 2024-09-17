package tn.esprit.PIDEV.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.PIDEV.Configurations.JwtService;
import tn.esprit.PIDEV.entities.ERole;
import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.repositories.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements IServiceUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Path rootLocation = Paths.get("images");


    public User findByUsernameLike(String username) {
        return userRepository.findByUsernameLike(username);
    }



    @Override
    public String saveImageForUsers(MultipartFile file) throws IOException {
        if(file.isEmpty()){
            throw new IOException("l'image est null");
        }
        String filePath = file.getOriginalFilename();
        String fileExtention = filePath.substring(filePath.lastIndexOf("."));
        String fileName = UUID.randomUUID()+fileExtention;

        Path fileDestination = this.rootLocation
                .resolve(fileName).normalize().toAbsolutePath();


        if(!fileDestination.getParent().equals(this.rootLocation.toAbsolutePath())){
            throw new IOException("mouvaise destination");
        }

        try(InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream , fileDestination , StandardCopyOption.REPLACE_EXISTING);
        }

        String fileUrl = "/images/"+fileName;
        return fileUrl;
    }



    public void verifyPhoneNumber(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (code.equals(user.getVerificationCode())) {
            user.setTelVerif(true);
            userRepository.save(user);
            // You can add additional logic here if needed
        } else {
            // Handle incorrect verification code
            throw new RuntimeException("Incorrect verification code");
        }
    }
    @Override
    public Boolean blockUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if(user.isActivated()){
            user.setActivated(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public List<User> getAllUserWithRole(ERole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public boolean createPasswordResetToken(String token, UserDetails userDetails) {
        return jwtService.isTokenValid(token,userDetails);
    }

    @Override
    public String generateToken(User user) {
        return null;
    }


    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

