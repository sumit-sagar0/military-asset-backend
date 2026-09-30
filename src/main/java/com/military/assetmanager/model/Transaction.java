package com.military.assetmanager.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data @Entity public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) private TransactionType type;
    @ManyToOne @JoinColumn(name = "asset_id") private Asset asset;
    private Integer quantity;
    @ManyToOne @JoinColumn(name = "from_base_id") private Base fromBase;
    @ManyToOne @JoinColumn(name = "to_base_id") private Base toBase;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();
}