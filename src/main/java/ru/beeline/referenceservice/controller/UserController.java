/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.referenceservice.dto.PasswordDTO;
import ru.beeline.referenceservice.dto.UserRequestDTO;
import ru.beeline.referenceservice.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ApiOperation(value = "Создание пользователя")
    public ResponseEntity createUser(@RequestBody UserRequestDTO userRequest) {
        userService.createUser(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/password")
    @ApiOperation(value = "Смена пароля")
    public ResponseEntity patchUser(@PathVariable(name = "id") Integer id,
                                    @RequestBody PasswordDTO passwordDTO) {
        userService.passwordChange(id, passwordDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
