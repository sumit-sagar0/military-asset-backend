package com.military.assetmanager.dto;
import lombok.Data;
@Data
public class TransferRequest {
    private Long assetId;
    private Long fromBaseId;
    private Long toBaseId;
    private Integer quantity;
    private String details;
}