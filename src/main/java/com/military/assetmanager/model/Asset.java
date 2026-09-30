package com.military.assetmanager.model;
import jakarta.persistence.*;
import lombok.Data;
@Data @Entity public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    @ManyToOne @JoinColumn(name = "category_id") private AssetCategory category;
}