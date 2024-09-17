package tn.esprit.PIDEV.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserProfileRequest {
    private String oldPassword;
    private String newPassword;
    private String username;
    private String email;
    private String tel;
    private String image;
}
