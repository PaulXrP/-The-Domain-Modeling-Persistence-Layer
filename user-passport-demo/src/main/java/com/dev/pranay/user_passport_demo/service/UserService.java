package com.dev.pranay.user_passport_demo.service;

import com.dev.pranay.user_passport_demo.dtos.PassportDto;
import com.dev.pranay.user_passport_demo.dtos.UserDto;
import com.dev.pranay.user_passport_demo.models.Passport;
import com.dev.pranay.user_passport_demo.models.User;
import com.dev.pranay.user_passport_demo.repository.PassportRepository;
import com.dev.pranay.user_passport_demo.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    @Transactional // <<< Crucial: Ensures operations run within a transaction
    public UserDto createOrUpdateUser(UserDto userDto) {
        User user;

        // --- Update existing user path ---
        if (userDto.getId() != null) {
            // Find the existing user or throw an exception if not found
            user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new RuntimeException("User not found for update with ID: " + userDto.getId()));

            // Update basic user fields from DTO
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());

            // --- Refined Passport Handling Logic within Update ---
            PassportDto passportDto = userDto.getPassportDto();
            Passport existingPassport = user.getPassport();

            if (passportDto != null) {
                // Passport details ARE provided in the request DTO

                if (existingPassport == null) {
                    // User didn't have a passport, create and associate a new one
                    Passport newPassport = new Passport();
                    // Map details from DTO to the new Passport entity
                    newPassport.setPassportNumber(passportDto.getPassportNumber());
                    newPassport.setNationality(passportDto.getNationality());
                    // ID and timestamps will be handled by JPA (@GeneratedValue, @CreationTimestamp, @PrePersist)
                    // IMPORTANT: Associate the new passport with the user *before* saving the user.
                    user.setPassport(newPassport); // CascadeType.ALL will handle saving the newPassport
                } else {
                    // User already has a passport, update its details

                    // Optional but Recommended: Check for ID mismatch if DTO provides one
                    if (passportDto.getId() != null && !passportDto.getId().equals(existingPassport.getId())) {
                        throw new RuntimeException("Passport ID mismatch: Cannot update user's passport with ID "
                                + existingPassport.getId() + " using details for passport ID " + passportDto.getId());
                    }

                    // Update fields of the existing passport entity
                    existingPassport.setPassportNumber(passportDto.getPassportNumber());
                    existingPassport.setNationality(passportDto.getNationality());
                    // DO NOT manually set the ID here: existingPassport.setId(passportDto.getId());
                    // The existing passport keeps its original ID.
                    // Note: @UpdateTimestamp on User entity handles user modification time.
                    // If you need separate tracking for passport updates, add @UpdateTimestamp to Passport entity.
                }

            } else {
                // Passport details ARE NOT provided in the request DTO

                if (existingPassport != null) {
                    // Request wants to remove the passport information.
                    // Setting passport to null and relying on 'orphanRemoval = true'
                    // in the User entity's @OneToOne mapping will delete the Passport record.
                    user.setPassport(null);
                }
                // If existingPassport was already null, do nothing.
            }
            // --- End Refined Passport Handling ---

        }
        // --- Create new user path ---
        else {
            // ID in DTO is null, so create a new User entity
            user = modelMapper.map(userDto, User.class);
            // Ensure ModelMapper configuration correctly maps nested PassportDto to a new Passport object
            // if passportDto is present in userDto. CascadeType.ALL on User.passport
            // will ensure the new Passport is persisted along with the new User.
            // ID and timestamps for User and Passport will be generated by JPA.
        }

        // Persist changes: For updates, Hibernate detects changes to the managed 'user'
        // (and potentially its associated 'passport') within the transaction and issues SQL UPDATE(s).
        // For creates, it issues SQL INSERT(s). CascadeType.ALL handles the passport.
        User savedUser = userRepository.save(user);

        // Convert the persisted entity back to DTO for the response
        return convertToDto(savedUser);
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

        return convertToDto(savedUser);
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

    public UserDto fetchUserWithPassport(Long id) {
        User user = userRepository.fetchUserWithPassport(id);
        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> findAllUsersWithPassports() {
        List<User> allUsersWithPassports = userRepository.findAllUsersWithPassports();
        return allUsersWithPassports.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}
