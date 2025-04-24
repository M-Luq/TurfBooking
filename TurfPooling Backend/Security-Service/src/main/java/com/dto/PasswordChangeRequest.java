package com.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequest {

    @Size(min=8)
    private String newPassword;

    //getters and setters.
}
