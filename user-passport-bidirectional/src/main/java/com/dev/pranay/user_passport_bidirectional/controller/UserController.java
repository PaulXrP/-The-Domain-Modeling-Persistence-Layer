package com.dev.pranay.user_passport_bidirectional.controller;

import com.dev.pranay.user_passport_bidirectional.dtos.PassportDto;
import com.dev.pranay.user_passport_bidirectional.dtos.UserDto;
import com.dev.pranay.user_passport_bidirectional.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto user = userService.createUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto userDto = userService.getUser(id);
        return new ResponseEntity<>(userDto, HttpStatus.FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String deletedUser = userService.deleteUser(id);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

}
