package tn.esprit.PIDEV.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyVerificationCodeRequest {
    private String username ;
    private String verificationCode;
}

