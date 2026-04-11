package br.com.fiap.auth_service.domain;

import br.com.fiap.auth_service.domain.exceptions.InvalidPasswordException;
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
        validatePassword(password);
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

    private static final int MIN_PASSWORD_LENGTH = 12; //TODO parametrizar

    public static void validatePassword(String password) {

        String INVALID_PASSWORD_MSG = "A senha deve conter no minímo %d caracteres, incluindo letra maiúscula, minúscula, número e símbolo.";

        String formattedErrorMsg = String.format(INVALID_PASSWORD_MSG, MIN_PASSWORD_LENGTH);

        if (password == null) {
            throw new InvalidPasswordException(
                    formattedErrorMsg
            );
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSymbol = password.chars().anyMatch(
                ch -> !Character.isLetterOrDigit(ch)
        );

        if (!(password.length() >= MIN_PASSWORD_LENGTH
                && hasUppercase
                && hasLowercase
                && hasDigit
                && hasSymbol)) {
            throw new InvalidPasswordException(
                    formattedErrorMsg);
        }
    }
}
