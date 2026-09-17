package com.example.AuthJwt.Models;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "compte_oauth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompteOAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProviderOAuth provider;

    @Column(nullable = false)
    private String providerId;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
