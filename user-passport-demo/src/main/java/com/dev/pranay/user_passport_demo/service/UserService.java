package com.dev.pranay.user_passport_demo.service;

import com.dev.pranay.user_passport_demo.dtos.PassportDto;
import com.dev.pranay.user_passport_demo.dtos.UserDto;
import com.dev.pranay.user_passport_demo.models.Passport;
import com.dev.pranay.user_passport_demo.models.User;
import com.dev.pranay.user_passport_demo.repository.PassportRepository;
import com.dev.pranay.user_passport_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PassportRepository passportRepository;
    private final ModelMapper modelMapper;

    public UserDto createUser(UserDto userDto) {
        if (userDto.getId() != null && userRepository.existsById(userDto.getId())) {
            throw new RuntimeException("User with ID already exists: " + userDto.getId());
        }

        User mappedUser = modelMapper.map(userDto, User.class);

        // If the passportDto exists, convert and set it to the User entity
        if(userDto.getPassportDto() != null) {
            Passport passport = modelMapper.map(userDto.getPassportDto(), Passport.class);
            mappedUser.setPassport(passport);
        }

        User savedUser = userRepository.save(mappedUser);

//        return modelMapper.map(savedUser, UserDto.class);
        return convertToDto(savedUser);
    }

    private UserDto convertToDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        UserDto userDto = modelMapper.map(user, UserDto.class);

        if(user.getPassport() != null) {
            PassportDto passportDto = modelMapper.map(user.getPassport(), PassportDto.class);
            userDto.setPassportDto(passportDto);
        }
        return userDto;
    }

//    public UserDto createOrUpdateUser(UserDto userDto) {
//        User user = userDto.getId() != null
//                ? userRepository.findById(userDto.getId()).orElse(new User())
//                : new User();
//
//        modelMapper.map(userDto, user);
//
//        User savedUser = userRepository.save(user);
//
//        return modelMapper.map(savedUser, UserDto.class);
//    }

    public UserDto createOrUpdateUser(UserDto userDto) {
        User user;

        if (userDto.getId() != null) {
            user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new RuntimeException("User with ID " + userDto.getId() + " not found"));
        } else {
            user = new User();
        }

        modelMapper.map(userDto, user);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

//
//    public UserDto createOrUpdateUser(Long id, UserDto userDto) {
//        User user = userRepository.findById(id).orElse(new User());
//
//        // Handle Passport only if the passportDto is not null
//        if (userDto.getPassportDto() != null) {
//            if (user.getPassport() == null) {
//                // Create a new passport if user does not have one
//                Passport passport = modelMapper.map(userDto.getPassportDto(), Passport.class);
//                user.setPassport(passport);
//            } else {
//                // If passport exists, update its fields without altering the ID
//                Passport existingPassport = user.getPassport();
//                modelMapper.map(userDto.getPassportDto(), existingPassport);  // Update the passport
//
//                // If the Passport entity already has an ID, ensure it is not being re-created
//                existingPassport.setId(existingPassport.getId()); // No change in the ID
//                passportRepository.save(existingPassport);  // Save the updated passport explicitly
//            }
//        }
//
//        // Map other fields from the userDto to the user
//        modelMapper.map(userDto, user);
//
//        // Save the user (this will persist the user and its passport)
//        User savedUser = userRepository.save(user);
//
//        // Return the saved user as a DTO
//        return modelMapper.map(savedUser, UserDto.class);
//    }

    public UserDto createOrUpdateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElse(new User());

        // Update basic user fields (excluding passport)
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        // Handle Passport only if the passportDto is not null
        if (userDto.getPassportDto() != null) {
            if (user.getPassport() == null) {
                // User doesn't have a passport yet
                Passport newPassport = modelMapper.map(userDto.getPassportDto(), Passport.class);
                user.setPassport(newPassport);
            } else {
                // Update existing passport without changing ID
                Passport existingPassport = user.getPassport();
                existingPassport.setPassportNumber(userDto.getPassportDto().getPassportNumber());
                existingPassport.setNationality(userDto.getPassportDto().getNationality());
                // No need to call passportRepository.save(), assuming cascade is configured
            }
        }

        // Save the user (and the passport if cascade is enabled)
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDto.class);
    }




    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found with given id: " + id));

        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = userList.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return userDtoList;
    }

    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new RuntimeException("User not found...");
        }
        userRepository.deleteById(id);
    }
}
