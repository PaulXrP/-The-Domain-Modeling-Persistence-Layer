package com.dev.pranay.user_passport_bidirectional.service;

import com.dev.pranay.user_passport_bidirectional.dtos.PassportDto;
import com.dev.pranay.user_passport_bidirectional.dtos.UserDto;
import com.dev.pranay.user_passport_bidirectional.mappers.UserMapper;
import com.dev.pranay.user_passport_bidirectional.models.Passport;
import com.dev.pranay.user_passport_bidirectional.models.User;
import com.dev.pranay.user_passport_bidirectional.repository.PassportRepository;
import com.dev.pranay.user_passport_bidirectional.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PassportRepository passportRepository;

//    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        Passport passport = user.getPassport();
        // Setup bidirectional link
        if (passport != null) {
            user.setPassport(passport);
            passport.setUser(user);
        }

        System.out.println("Passport inside user before saving: " + user.getPassport());
        System.out.println("User inside passport: " + (user.getPassport() != null ? user.getPassport().getUser() : null));

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

    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtos = userList.stream()
                .map(user -> UserMapper.toDto(user))
                .collect(Collectors.toList());
        return userDtos;
    }

//    public UserDto createOrUpdateUser(Long id, UserDto userDto) {
//        User user =  (id !=null) ?
//                    userRepository.findById(id).orElse(new User()) :
//                    new User();
//
//        user.setName(userDto.getName());
//        user.setEmail(userDto.getEmail());
//
//        if(userDto.getPassportDto() != null) {
//            Passport passport = (user.getPassport() != null) ? user.getPassport() : new Passport();
//            passport.setPassportNumber(userDto.getPassportDto().getPassportNumber());
//            passport.setNationality(userDto.getPassportDto().getNationality());
//
//            passport.setUser(user);
//            user.setPassport(passport);
//        }
//
//        return UserMapper.toDto(userRepository.save(user));
//    }

    public UserDto createOrUpdateUser(Long id, UserDto userDto) {
        // Find user by id, if not found create a new user
        User user = (id != null) ?
                userRepository.findById(id).orElse(new User()) :
                new User();

        // Update user details
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        if (userDto.getPassportDto() != null) {
            // Check if the passport number already exists in the database
            Passport existingPassport = passportRepository.findByPassportNumber(userDto.getPassportDto().getPassportNumber());

            if (existingPassport != null) {
                // If passport exists, update the passport details and associate it with the user
                existingPassport.setNationality(userDto.getPassportDto().getNationality());
                // You can update other fields like passport number if needed, but passport number should ideally remain unique
                // existingPassport.setPassportNumber(userDto.getPassportDto().getPassportNumber()); // if updating the passport number is allowed

                user.setPassport(existingPassport);
                existingPassport.setUser(user);
            } else {
                // If passport doesn't exist, create a new one and associate it with the user
                Passport newPassport = new Passport();
                newPassport.setPassportNumber(userDto.getPassportDto().getPassportNumber());
                newPassport.setNationality(userDto.getPassportDto().getNationality());
                newPassport.setUser(user);

                user.setPassport(newPassport);
            }
        }

            // Save user and return the DTO
            return UserMapper.toDto(userRepository.save(user));

    }

}
