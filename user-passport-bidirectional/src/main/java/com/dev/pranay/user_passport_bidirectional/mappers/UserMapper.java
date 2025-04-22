package com.dev.pranay.user_passport_bidirectional.mappers;

import com.dev.pranay.user_passport_bidirectional.dtos.PassportDto;
import com.dev.pranay.user_passport_bidirectional.dtos.UserDto;
import com.dev.pranay.user_passport_bidirectional.models.Passport;
import com.dev.pranay.user_passport_bidirectional.models.User;

public class UserMapper {

    public static User toEntity(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        if(userDto.getPassportDto() != null) {
            Passport passport = new Passport();
            passport.setPassportNumber(userDto.getPassportDto().getPassportNumber());
            passport.setNationality(userDto.getPassportDto().getNationality());

            user.setPassport(passport);
            passport.setUser(user);
        }

        return user;
    }

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        if(user.getPassport() != null) {
            PassportDto passportDto = new PassportDto();
            passportDto.setPassportNumber(user.getPassport().getPassportNumber());
            passportDto.setNationality(user.getPassport().getNationality());

            userDto.setPassportDto(passportDto);
        }

        return userDto;
    }
}
