package ru.beeline.referenceservice.service;

import org.springframework.stereotype.Service;
import ru.beeline.referenceservice.domain.User;
import ru.beeline.referenceservice.dto.UserRequestDTO;
import ru.beeline.referenceservice.exception.LoginAlreadyExistsException;
import ru.beeline.referenceservice.exception.ValidationException;
import ru.beeline.referenceservice.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(UserRequestDTO userRequest) {
        loginValidate(userRequest.getLogin());
        if (userRepository.findByLogin(userRequest.getLogin()).isPresent()) {
            throw new LoginAlreadyExistsException("Логин уже занят");
        }
        String hashedPassword = hashSHA256(userRequest.getLogin());
        userRepository.save(User.builder()
                .login(userRequest.getLogin())
                .password(hashedPassword)
                .admin(userRequest.getAdmin())
                .build());
    }

    private void loginValidate(String login) {
        if (login == null || login.isEmpty()) {
            throw new ValidationException("Логин не должен быть пустым");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелов");
        }
        if (!login.matches("^[a-zA-Z0-9]{1,255}$")) {
            throw new ValidationException("Логин должен содержать только латинские буквы и цифры, максимум 255 символов");
        }
    }

    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String getUser(UserRequestDTO userRequest) {
        return "getUser";
    }

    public String User() {
        return "postUser";
    }
}