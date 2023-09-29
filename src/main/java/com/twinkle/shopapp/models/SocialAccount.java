package com.twinkle.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    @Column(name = "provider_id", length = 50, nullable = false)
    private String providerId;

    @Column(name = "name", length = 100) // maximum chữ là 350 chữ
    private String name;

    @Column(name = "email", length = 150) // maximum chữ là 350 chữ
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;





}
