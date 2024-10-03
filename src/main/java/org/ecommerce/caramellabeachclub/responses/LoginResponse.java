package org.ecommerce.caramellabeachclub.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class LoginResponse {

    @Getter
    private String token;

    private long expiresIn;

}

