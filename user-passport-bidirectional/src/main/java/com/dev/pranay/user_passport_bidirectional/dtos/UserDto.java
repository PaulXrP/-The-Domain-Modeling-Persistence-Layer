package com.dev.pranay.user_passport_bidirectional.dtos;

import lombok.Data;

@Data
public class UserDto {

    private String name;
    private String email;
    private PassportDto passportDto;
}
