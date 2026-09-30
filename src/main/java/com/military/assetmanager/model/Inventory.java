package com.military.assetmanager.model;
import jakarta.persistence.*;
import lombok.Data;
@Data @Entity public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "base_id") private Base base;
    @ManyToOne @JoinColumn(name = "asset_id") private Asset asset;
    private Integer quantity;
}