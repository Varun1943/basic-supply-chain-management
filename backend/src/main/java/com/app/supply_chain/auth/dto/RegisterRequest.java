package com.app.supply_chain.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;
    private String password;
}