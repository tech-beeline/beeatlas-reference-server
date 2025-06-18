package ru.beeline.referenceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.referenceservice.domain.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findByLoginAndPassword(String login, String password);

    Optional<UserEntity> findByIdAndLogin(Integer id, String login);
}