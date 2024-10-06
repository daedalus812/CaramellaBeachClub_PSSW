package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
@Entity
@Table(name = "utente")
public class Utente implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utente", nullable = false)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Getter
    @Setter
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @OneToMany(mappedBy = "idUtente", fetch = FetchType.EAGER)
    private Set<Ordine> ordines = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUtente", fetch = FetchType.EAGER)
    private Set<Recensione> recensiones = new LinkedHashSet<>();

    @Column(name = "role")
    private String role;

    @Getter
    private boolean enabled;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpireAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role); // Aggiunge il prefisso "ROLE_" al ruolo
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}