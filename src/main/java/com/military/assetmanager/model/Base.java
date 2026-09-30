package com.military.assetmanager.model;
import jakarta.persistence.*;
import lombok.Data;
@Data @Entity public class Base {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String location;
}