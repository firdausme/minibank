package com.introstudio.minibank.message.response;

import lombok.*;

@Getter
@Setter
@Builder
public class JwtResponse {

    private String token;

    private String type;

}
