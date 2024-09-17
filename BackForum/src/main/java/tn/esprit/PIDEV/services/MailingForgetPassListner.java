package tn.esprit.PIDEV.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import tn.esprit.PIDEV.repositories.CandidatureRepository;
import tn.esprit.PIDEV.entities.User;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailingForgetPassListner implements ApplicationListener<MailingEventForgetPassword> {

    private final  JavaMailSender javaMailSender;
    private final UserServiceImpl userService;
    private final CandidatureRepository candidatureRepository;
    private   User user;

    @Override
    public void onApplicationEvent(MailingEventForgetPassword event) {
        user = event.getUser();
    }
/*
    private void sendMaildePostulation(Candidature candidature, HttpServletRequest servletRequest) throws MessagingException, UnsupportedEncodingException {

        String subject = "Postulé(e)";
        String senderName = "BUGBUTTLERS";
        String mailContent = "<p> Hi, " + candidature.getUser().getUsername() + ", </p> <br>" +
                "<p><b><br>Vous étes insscrits à la candidature</b>" + candidature.getOffre().getIntitule()+" <br>" +
                "Bon Courage </p> <br>" +

                "<p> Forum des Entreprises d'ESPRIT";

        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("piemepieme494@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
        candidatureRepository.save(candidature);

    }*/
}
