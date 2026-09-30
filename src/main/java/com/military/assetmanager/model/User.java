package com.military.assetmanager.model;
import jakarta.persistence.*;
import lombok.Data;
@Data @Entity @Table(name="users") public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true) private String email;
    private String password;
    private String name;
    @Enumerated(EnumType.STRING) private Role role;
    @ManyToOne @JoinColumn(name = "base_id") private Base base;
}