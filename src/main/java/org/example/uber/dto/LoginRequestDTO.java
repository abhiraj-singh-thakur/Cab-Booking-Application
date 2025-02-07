package org.example.uber.dto;

import com.sun.jdi.connect.Connector;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
