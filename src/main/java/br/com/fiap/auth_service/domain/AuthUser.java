package br.com.fiap.auth_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int tokenVersion;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = true)
    private Instant passwordUpdatedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateCreated;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant lastUpdated;

    public static AuthUser newUser(String login, String password){
        return new AuthUser(
                null,
                login,
                password,
                1,
                true,
                null,
                null,
                null
        );
    }
}
