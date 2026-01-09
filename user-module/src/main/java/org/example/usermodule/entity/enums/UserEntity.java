package org.example.usermodule.entity.enums;

import jakarta.persistence.*;
import lombok.*;
import org.example.usermodule.entity.enums.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "number_phone", unique = true, nullable = false)
    private String numberPhone;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(columnDefinition = "TIMESTAMP", name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(columnDefinition = "TIMESTAMP", name = "time_stamp")
    private LocalDateTime timeStamp;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Override
    public boolean isEnabled() {
        return isEnabled;  // ← ИСПРАВИТЬ: возвращать поле, не super
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
