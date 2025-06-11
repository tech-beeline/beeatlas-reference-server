package ru.beeline.referenceservice.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{1,255}$", message = "Логин должен содержать только буквы и цифры, максимальная длина - 255")
    private String login;

    private Boolean admin;
}
