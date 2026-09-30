package com.military.assetmanager.dto;
import lombok.Data;
@Data
public class PurchaseRequest {
    private Long assetId;
    private Long baseId;
    private Integer quantity;
    private String details;
}