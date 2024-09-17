package tn.esprit.PIDEV.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PIDEV.Configurations.JwtService;
import tn.esprit.PIDEV.Configurations.SecurityPrincipale;
import tn.esprit.PIDEV.entities.AuthenticateResponse;
import tn.esprit.PIDEV.entities.RegisterWithGoogleRequest;
import tn.esprit.PIDEV.entities.RegesterRequest;
import tn.esprit.PIDEV.entities.VerifyVerificationCodeRequest;
import tn.esprit.PIDEV.entities.PasswordResetUtil;
import tn.esprit.PIDEV.entities.EntityResponse;
import tn.esprit.PIDEV.entities.AuthenticationRequest;
import  tn.esprit.PIDEV.entities.ModifyUserProfileRequest;
import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.services.AuthenticationService;
import tn.esprit.PIDEV.services.UserServiceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationRestController {

    public UserServiceImpl userService;
    public AuthenticationService authenticationService;

    private JwtService jwtService;



    @PostMapping("/register")
    public ResponseEntity<AuthenticateResponse> register(
            @RequestBody RegesterRequest request, HttpServletRequest servletRequest){
        log.info("registered");
        try {
            return ResponseEntity.ok(authenticationService.register(request, servletRequest).getBody());
        } catch (MessagingException | UnsupportedEncodingException e) {

            log.error("Error registering user:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/registerExposant")
    public ResponseEntity<AuthenticateResponse> registerExposant(
            @RequestBody RegesterRequest request, HttpServletRequest servletRequest){
        log.info("registered");
        try {
            return ResponseEntity.ok(authenticationService.registerExposant(request, servletRequest).getBody());
        } catch (MessagingException | UnsupportedEncodingException e) {

            log.error("Error registering user:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticateResponse response = authenticationService.authenticate(request).getBody();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/is-authenticated")
    public  ResponseEntity<Object> isUserAuthenticated(){
        User principale = SecurityPrincipale.getInstance().getLoggedInPrincipal();
        if(principale!=null){
            return EntityResponse.generateResponse("Authorized", HttpStatus.OK , principale);
        }
        return EntityResponse.generateResponse("Unauthorized",HttpStatus.NOT_FOUND, false);
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(){

        User user = userService.findByUsernameLike(SecurityPrincipale.getInstance().getLoggedInPrincipal().username);
        return EntityResponse.generateResponse("Success",HttpStatus.OK , user);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request , HttpServletResponse response)throws IOException{
        authenticationService.refreshToken(request,response);
    }






    @PostMapping("/reset-password")
    public String restPassword(@RequestBody PasswordResetUtil passwordResetUtil,
                               @RequestParam("token") String token){
        User user =
                userService.findByUsernameLike(jwtService.extractUsername(token));


        if(user!=null){
            userService.changePassword(user, passwordResetUtil.getNewPassword());
            return "password reset success";
        }
        return "Invalid password reset token";
    }


    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"
                +request.getServerPort()+request.getContextPath();
    }

    @PostMapping("/verify-verification-code")
    public ResponseEntity<String> verifyVerificationCode(@RequestBody VerifyVerificationCodeRequest request) {
        try {
            authenticationService.verifyVerificationCode(request.getUsername(), request.getVerificationCode());
            return ResponseEntity.ok("Verification successful. User account activated.");
        } catch (RuntimeException e) {
            log.error("Error verifying verification code:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed. " + e.getMessage());
        }
    }
    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email) {
        try {
            authenticationService.forgotPassword(email);
            return ResponseEntity.ok("New password sent to the user's email.");
        } catch (Exception e) {
            log.error("Error processing forgot password request:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing forgot password request.");
        }
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = authenticationService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        authenticationService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/users/{userId}")
    public ResponseEntity<?> modifyUser(@PathVariable Long userId, @RequestBody RegesterRequest request) {
        return authenticationService.modifyUser(userId, request);
    }
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> modifyUserProfile(@PathVariable Long userId, @RequestBody ModifyUserProfileRequest request) {
        return authenticationService.modifyUserProfile(userId, request);
    }

    @PostMapping("/activateExposant/{userId}")
    public ResponseEntity<?> activateExposant(@PathVariable Long userId) {
        try {
            return authenticationService.activateExposant(userId);
        } catch (RuntimeException e) {
            log.error("Error activating exposant account:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error activating exposant account.");
        }
    }
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam Long userId, @RequestParam String code) {
        authenticationService.verifyPhoneNumber(userId, code);
        return ResponseEntity.ok().build();
    }
}
