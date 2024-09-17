package tn.esprit.PIDEV.controllers;


import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.PIDEV.entities.NotificationMessage;

@RestController
@AllArgsConstructor
public class NotificationController {

    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/application")
    @SendTo("/all/messages")
    public String send(final String message) throws Exception {
        return message;
    }
    @MessageMapping("/applications")
    public void sendNotificationToUser(NotificationMessage message) throws Exception {
        String destination = "/specific/user/" + message.getUserId();
        simpMessagingTemplate.convertAndSend(destination, message.getMessage());
    }

    @MessageMapping("/appli")
    public void sendNotificationToAdmin(NotificationMessage message) throws Exception {
        String destination = "/admin/" + message.getUserId();
        simpMessagingTemplate.convertAndSend(destination, message.getMessage());
    }
}
