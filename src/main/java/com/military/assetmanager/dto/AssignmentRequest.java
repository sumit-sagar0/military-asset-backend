package com.military.assetmanager.dto;
import lombok.Data;
@Data
public class AssignmentRequest {
    private Long assetId;
    private Long baseId;
    private Integer quantity;
    private String type; // ASSIGNMENT or EXPENDITURE
    private String details; // Personnel name or reason
}