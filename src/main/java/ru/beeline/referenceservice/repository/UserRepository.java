package ru.beeline.referenceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.referenceservice.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndPassword(String login, String password);
}