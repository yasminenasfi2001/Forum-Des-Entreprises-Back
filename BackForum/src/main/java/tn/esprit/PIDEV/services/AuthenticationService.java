package tn.esprit.PIDEV.services;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.warrenstrange.googleauth.GoogleAuthenticator;
        import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
        import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
        import jakarta.annotation.PostConstruct;
        import jakarta.mail.MessagingException;
        import jakarta.mail.internet.MimeMessage;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import lombok.AllArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import net.glxn.qrgen.core.image.ImageType;
        import net.glxn.qrgen.javase.QRCode;
        import org.springframework.context.ApplicationEventPublisher;
        import org.springframework.core.io.ByteArrayResource;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.mail.javamail.JavaMailSender;
        import org.springframework.mail.javamail.MimeMessageHelper;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;
        import tn.esprit.PIDEV.Configurations.JwtService;
        import tn.esprit.PIDEV.entities.*;
        import tn.esprit.PIDEV.repositories.UserRepository;
        import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
        import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
        import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
        import com.google.api.client.json.JsonFactory;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import lombok.extern.slf4j.Slf4j;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.UnsupportedEncodingException;
        import java.security.GeneralSecurityException;
        import java.security.SecureRandom;
        import java.util.Collections;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;
@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender javaMailSender;
    private final ApplicationEventPublisher eventPublisher;

    private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private GoogleIdTokenVerifier verifier;
    private final PhoneVerificationService twilioService;



    public ResponseEntity<?> verifyPhoneNumber(Long userId, String userInputCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify the code entered by the user
        if (userInputCode.equals(user.getTelVerifcode())) {
            // Update telVerif field to true if verification succeeds
            user.setTelVerif(true);
            userRepository.save(user);
            return ResponseEntity.ok("Phone number verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }
    }




    @Transactional
    public ResponseEntity<AuthenticateResponse> register(RegesterRequest request, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {
        // Generate a secret key for 2FA
        String verificationCode = generateVerificationCode(6);
        String secretKey = generate2FASecretKey();

        // Create the user with 2FA enabled and set the secret key
        var user = User.builder()
                .username(request.getUsername())
                .activated(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tel(request.getTel())
                .image(request.getImage())
                .role(request.role.ROLE_ETUDIANT)
                .verificationCode(generateVerificationCode(6))
                .telVerifcode(verificationCode)
                .activation2fa(true)
                .telVerif(false)
                .twofactorcode(secretKey) // Set the 2FA secret key for the user

                .build();
        userRepository.save(user);

        // Send verification code via Twilio
        twilioService.sendVerificationCode(user.getTel(), verificationCode);

        // Schedule deactivation task
        scheduleDeactivation(user);
        userRepository.save(user);
     /*   if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }*/
        // Send email with 2FA code and QR code
        send2FACodeEmail(user);

        // Schedule deactivation task


        // Generate JWT tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Create response
        AuthenticateResponse response = AuthenticateResponse.builder()
                .acessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(response);
    }
    private void scheduleDeactivation(User user) {
        TimerTask task = new TimerTask() {
            public void run() {
                if (!user.isTelVerif()) {
                    // Deactivate account if phone number is not verified
                    user.setActivated(false);
                    userRepository.save(user);
                    // Log deactivation
                }
            }
        };

        Timer timer = new Timer(true);
        timer.schedule(task, 60 * 60 * 1000); // 1 hour delay for deactivation
    }


    private void send2FACodeEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "2FA Activation";
        String senderName = "BUGBUTTLERS";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>Thank you for registering with us!</p>" +
                "<p>Please use the following code to activate 2FA in your account:</p>" +
                "<p><b>" + user.getTwofactorcode() + "</b></p>" +
                "<p>Alternatively, you can scan the QR code below with the Google Authenticator app.</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);

        // Add the HTML content
        messageHelper.setText(mailContent, true);

        // Attach the QR code image
        byte[] qrCodeImageBytes = generateQRCode(user.getUsername(), user.getTwofactorcode());
        messageHelper.addInline("qrCode", new ByteArrayResource(qrCodeImageBytes), "image/png");

        javaMailSender.send(message);
    }

    private byte[] generateQRCode(String email, String secretKey) {
        // Construct the URL for the QR code
        String qrCodeUrl = "otpauth://totp/" + email + "?secret=" + secretKey;


        ByteArrayOutputStream outputStream = QRCode.from(qrCodeUrl)
                .to(ImageType.PNG)
                .stream();

        return outputStream.toByteArray();
    }
    private void scheduleDeactivationTask(User user) {
        TimerTask task = new TimerTask() {
            public void run() {

                if (user.getVerificationCodeCheck() == user.verificationCode) {
                    user.setActivated(false);
                    userRepository.save(user);
                    log.info("Account deactivated due to non-verification: {}", user.getUsername());
                }
            }
        };

        Timer timer = new Timer(true);
        timer.schedule(task, 100 * 30 * 1000);
    }

    private String generateVerificationCode(int length) {
        String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder verificationCode = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            verificationCode.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return verificationCode.toString();
    }

    private void sendVerificationEmail(User user, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {
        String verificationUrl = user.getVerificationCode();
        String subject = "Email Verification";
        String senderName = "BUGBUTTLERS";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p><b>Thank you for registering with us!</b>" + "" +
                "Please, follow the link below to verify your email address.</p>" +
                verificationUrl  +
                "<p> Your Application Name";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("piemepieme494@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
        userRepository.save(user);

    }





    public ResponseEntity<AuthenticateResponse> authenticate(AuthenticationRequest request) {
        log.info(request.getUsername());
        log.info(request.getPassword());
        log.info(String.valueOf(userRepository.findByUsername(request.getUsername())));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        log.info(String.valueOf(user == null) + "-");
        log.info("@" + user.getUsername());

        if (!user.isActivated()) {
            throw new RuntimeException("User account is not activated.");
        }

        // Check if 2FA is activated for the user
        if (user.isActivation2fa()) {
            // Perform 2FA verification
            boolean isVerified = verify2FA(user.getTwofactorcode(), request.getTwofactorcode());
            if (!isVerified) {
                throw new RuntimeException("Invalid two-factor authentication code.");
            }
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        AuthenticateResponse response = AuthenticateResponse.builder()
                .acessToken(jwtToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .tel(user.getTel())
                .image(user.getImage())
                .verificationCode(user.getVerificationCode())
                .role(user.getRole())
                .verificationCodeCheck(user.getVerificationCodeCheck())
                .build();

        return ResponseEntity.ok(response);
    }

    // Function to verify the 2FA code
    private boolean verify2FA(String secretKey, String code) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        return googleAuthenticator.authorize(secretKey, Integer.parseInt(code));
    }


    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken,  user)) {
                var accessToken = jwtService.generateToken( user);
                var authResponse = AuthenticateResponse.builder()
                        .acessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    @Transactional
    public void verifyVerificationCode(String username, String verificationCode) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (verificationCode.equals(user.getVerificationCode())) {
            user.setVerificationCodeCheck(verificationCode);
            user.setActivated(true);
            userRepository.save(user);
            log.info("Account activated: {}", username);
        } else {

            log.error("Incorrect verification code for user: {}", username);

            throw new RuntimeException("Incorrect verification code");
        }
    }
    @Transactional
    public void forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String newPassword = generateRandomPassword(10);


        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        sendNewPasswordEmail(user, newPassword);
    }
    private String generateRandomPassword(int length) {
        String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder newPassword = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            newPassword.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return newPassword.toString();
    }
    private void sendNewPasswordEmail(User user, String newPassword) throws MessagingException, UnsupportedEncodingException {
        String subject = "New Password";
        String senderName = "BUGBUTTLERS";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>Your new password is: <b>" + newPassword + "</b></p>" +
                "<p>Please keep this password secure and consider changing it after logging in.</p>" +
                "<p> Your Application Name";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }


    @Transactional
    public ResponseEntity<?> modifyUser(Long userId, RegesterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setTel(request.getTel());
        user.setImage(request.getImage());


        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
    public ResponseEntity<?> activate2FA(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String secretKey = generate2FASecretKey();

        user.setActivation2fa(true);
        user.setTwofactorcode(secretKey);

        userRepository.save(user);


        return ResponseEntity.ok("2FA activated. Secret key: " + secretKey);
    }


    public String generate2FASecretKey() {
        // Create a Google Authenticator instance
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(new GoogleAuthenticatorConfig());

        // Generate a secret key
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();

        // Return the secret key as a string
        return key.getKey();
    }
    public ResponseEntity<?> modifyUserProfile(Long userId, ModifyUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }

        // Update the user's account information
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setTel(request.getTel());
        user.setImage(request.getImage());

        // If a new password is provided, encode and update the password
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<AuthenticateResponse> registerExposant(RegesterRequest request, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {
        // Generate a secret key for 2FA
        String secretKey = generate2FASecretKey();

        // Create the user with 2FA enabled and set the secret key
        var user = User.builder()
                .username(request.getUsername())
                .activated(false) // Set activated to false for exposant
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tel(request.getTel())
                .image(request.getImage())
                .role(request.role.ROLE_EXPOSANT)
                .verificationCode(generateVerificationCode(6))
                .activation2fa(true)
                .twofactorcode(secretKey) // Set the 2FA secret key for the user
                .build();
        userRepository.save(user);
      /*  if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }*/
        // Send email with 2FA code and QR code
        send2FACodeEmailForExposant(user);

        // Schedule deactivation task

        // Generate JWT tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Create response
        AuthenticateResponse response = AuthenticateResponse.builder()
                .acessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(response);
    }

    private void send2FACodeEmailForExposant(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "2FA Activation";
        String senderName = "BUGBUTTLERS";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>Thank you for registering with us!</p>" +
                "<p>Please use the following code to activate 2FA in your account:</p>" +
                "<p><b>" + user.getTwofactorcode() + "</b></p>" +
                "<p>Alternatively, you can scan the QR code below with the Google Authenticator app.</p>" +
                "<p>Please note that your account is currently pending approval from the admin. You will be notified via email once your account is approved.</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);

        // Add the HTML content
        messageHelper.setText(mailContent, true);

        // Attach the QR code image
        byte[] qrCodeImageBytes = generateQRCode(user.getUsername(), user.getTwofactorcode());
        messageHelper.addInline("qrCode", new ByteArrayResource(qrCodeImageBytes), "image/png");

        javaMailSender.send(message);
    }
    @Transactional
    public ResponseEntity<?> activateExposant(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActivated(true);

        // Save the updated user
        userRepository.save(user);

        // Send email to the user
        try {
            sendExposantActivationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Handle any exceptions related to sending email
            e.printStackTrace(); // Log the error or handle it as needed
        }

        return ResponseEntity.ok("Exposant account activated: " + user.getUsername());
    }

    private void sendExposantActivationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Exposant Account Activation";
        String senderName = "BUGBUTTERS";
        String mailContent = "<p>Hi, " + user.getUsername() + ",</p>" +
                "<p>Your exposant account has been successfully activated.</p>" +
                "<p>You can now access your account and start using our services.</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("your-email@example.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

}