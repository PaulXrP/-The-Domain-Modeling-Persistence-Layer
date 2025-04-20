package com.dev.pranay.user_passport_demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassportDto {

    private Long id;
    private String passportNumber;
    private String nationality;
}
