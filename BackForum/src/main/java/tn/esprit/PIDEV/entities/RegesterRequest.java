package tn.esprit.PIDEV.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.PIDEV.entities.ERole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegesterRequest {

    public String username;
    public String email;
    public String password;
    public String tel;
    public String image;
    public ERole role;
}
