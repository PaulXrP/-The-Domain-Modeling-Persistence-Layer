package com.dev.pranay.user_passport_bidirectional.service;

import com.dev.pranay.user_passport_bidirectional.dtos.PassportDto;
import com.dev.pranay.user_passport_bidirectional.dtos.UserDto;
import com.dev.pranay.user_passport_bidirectional.mappers.UserMapper;
import com.dev.pranay.user_passport_bidirectional.models.Passport;
import com.dev.pranay.user_passport_bidirectional.models.User;
import com.dev.pranay.user_passport_bidirectional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        Passport passport = user.getPassport();
        // Setup bidirectional link
        if(passport != null) {
            user.setPassport(passport);
            passport.setUser(user);
        }
        User savedUser = userRepository.save(user); // Cascade saves passport
        return UserMapper.toDto(savedUser);
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found with given id: " + id));
        return UserMapper.toDto(user);
    }

    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return "User successfully deleted...";
    }
}
