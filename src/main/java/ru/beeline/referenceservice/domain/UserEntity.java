package ru.beeline.referenceservice.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(name = "user_id_generator", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    private String login;

    @Column(length = 64)
    private String password;

    private Boolean admin = false;
}