package tn.esprit.PIDEV.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import tn.esprit.PIDEV.entities.User;

@Getter
@Setter
public class MailingEventForgetPassword extends ApplicationEvent {

    private User user;
    private String applicationUrl;

    public MailingEventForgetPassword(User user , String applicationUrl) {
        super(user);
        this.user = user ;
        this.applicationUrl = applicationUrl;
    }
}
