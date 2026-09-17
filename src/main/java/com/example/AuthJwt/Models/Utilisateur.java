package com.example.AuthJwt.Models;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    private String motDePasse;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean actif = true;

    private String secret2FA;

    private boolean deuxFAActive = false;

    private LocalDateTime dateCreation = LocalDateTime.now();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompteOAuth> comptesOAuth;
}
