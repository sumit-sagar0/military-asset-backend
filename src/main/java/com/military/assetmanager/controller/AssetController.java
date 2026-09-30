package com.military.assetmanager.controller;
import com.military.assetmanager.dto.*;
import com.military.assetmanager.model.*;
import com.military.assetmanager.repository.*;
import com.military.assetmanager.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AssetController {
    @Autowired private AssetService assetService;
    @Autowired private AssetRepository assetRepo;
    @Autowired private AssetCategoryRepository categoryRepo;
    @Autowired private BaseRepository baseRepo;
    @Autowired private InventoryRepository inventoryRepo;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard(
        @RequestParam(required = false) Long baseId,
        @RequestHeader(value = "Authorization", required = false) String auth) {
        return ResponseEntity.ok(assetService.getDashboardStats(baseId));
    }

    @GetMapping("/assets")
    public ResponseEntity<List<Asset>> getAssets() {
        return ResponseEntity.ok(assetRepo.findAll());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<AssetCategory>> getCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }

    @GetMapping("/bases")
    public ResponseEntity<List<Base>> getBases() {
        return ResponseEntity.ok(baseRepo.findAll());
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getInventory(
        @RequestParam(required = false) Long baseId) {
        if (baseId != null) return ResponseEntity.ok(inventoryRepo.findByBaseId(baseId));
        return ResponseEntity.ok(inventoryRepo.findAll());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
        @RequestParam(required = false) Long baseId) {
        if (baseId != null) return ResponseEntity.ok(assetService.getTransactionsByBase(baseId));
        return ResponseEntity.ok(assetService.getTransactions());
    }

    @PostMapping("/purchases")
    public ResponseEntity<?> purchase(
        @RequestBody PurchaseRequest req,
        @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            return ResponseEntity.ok(assetService.recordPurchase(req, auth));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/transfers")
    public ResponseEntity<?> transfer(
        @RequestBody TransferRequest req,
        @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            return ResponseEntity.ok(assetService.recordTransfer(req, auth));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/assignments")
    public ResponseEntity<?> assignment(
        @RequestBody AssignmentRequest req,
        @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            return ResponseEntity.ok(assetService.recordAssignment(req, auth));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}