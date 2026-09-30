package com.military.assetmanager.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
@Data @AllArgsConstructor
public class DashboardResponse {
    private long totalAssets;
    private long totalPurchases;
    private long totalTransferIn;
    private long totalTransferOut;
    private long totalAssigned;
    private long totalExpended;
    private long netMovement;
    private long closingBalance;
}