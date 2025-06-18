package ru.beeline.referenceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.referenceservice.domain.User;
import org.springframework.stereotype.Repository;
import ru.beeline.referenceservice.domain.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findByIdAndLogin(Integer id, String login);
}