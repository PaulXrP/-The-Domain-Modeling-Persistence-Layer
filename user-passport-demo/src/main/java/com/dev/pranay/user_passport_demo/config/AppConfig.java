package com.dev.pranay.user_passport_demo.config;

import com.dev.pranay.user_passport_demo.dtos.UserDto;
import com.dev.pranay.user_passport_demo.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(User.class, UserDto.class).addMappings(m ->
                m.map(User::getPassport, UserDto::setPassportDto)
        );
        return mapper;
    }
}

