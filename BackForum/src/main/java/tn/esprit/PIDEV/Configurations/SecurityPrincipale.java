package tn.esprit.PIDEV.Configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import tn.esprit.PIDEV.entities.User;
import tn.esprit.PIDEV.services.UserServiceImpl;

import java.util.Collection;

@Service
@Slf4j
public class SecurityPrincipale {

    private static SecurityPrincipale securityPrincipale = null;
    private  Authentication principale =  SecurityContextHolder.getContext().getAuthentication();

    private static UserServiceImpl userService ;
    @Autowired
    private SecurityPrincipale(UserServiceImpl userService) {
        this.userService = userService;
    }

    public static SecurityPrincipale getInstance(){
        securityPrincipale = new SecurityPrincipale(userService);
        return securityPrincipale;
    }

    public User getLoggedInPrincipal(){
        if(principale!=null ){
            log.info("***************************************"+ principale.getPrincipal());
            User loggedInPrincipalObject = (User)principale.getPrincipal();

            return userService.findByUsernameLike(loggedInPrincipalObject.getUsername());
        }
        return null;
    }

    public Collection<?> getLoggedInPrincipalAuthorities() {
        return ((UserDetails)principale.getPrincipal()).getAuthorities();
    }

}
