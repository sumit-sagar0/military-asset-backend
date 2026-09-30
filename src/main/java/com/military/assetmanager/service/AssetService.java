package com.military.assetmanager.service;
import com.military.assetmanager.dto.*;
import com.military.assetmanager.model.*;
import com.military.assetmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AssetService {
    @Autowired private AssetRepository assetRepo;
    @Autowired private BaseRepository baseRepo;
    @Autowired private InventoryRepository inventoryRepo;
    @Autowired private TransactionRepository transactionRepo;
    @Autowired private UserRepository userRepo;

    private User getFirstAdminUser() {
        return userRepo.findAll().stream().findFirst()
            .orElseThrow(() -> new RuntimeException("No users found"));
    }

    // Helper to resolve user from token header
    public User resolveUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return getFirstAdminUser();
        }
        try {
            String token = authHeader.substring(7);
            String decoded = new String(java.util.Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            Long userId = Long.parseLong(parts[0]);
            return userRepo.findById(userId).orElse(getFirstAdminUser());
        } catch (Exception e) {
            return getFirstAdminUser();
        }
    }

    @Transactional
    public Transaction recordPurchase(PurchaseRequest req, String authHeader) {
        Asset asset = assetRepo.findById(req.getAssetId()).orElseThrow();
        Base base = baseRepo.findById(req.getBaseId()).orElseThrow();
        User user = resolveUser(authHeader);

        // Update or create inventory
        Inventory inv = inventoryRepo.findByBaseIdAndAssetId(base.getId(), asset.getId())
            .orElse(new Inventory());
        inv.setBase(base);
        inv.setAsset(asset);
        inv.setQuantity((inv.getQuantity() == null ? 0 : inv.getQuantity()) + req.getQuantity());
        inventoryRepo.save(inv);

        // Log transaction (API Logging)
        Transaction txn = new Transaction();
        txn.setType(TransactionType.PURCHASE);
        txn.setAsset(asset);
        txn.setQuantity(req.getQuantity());
        txn.setToBase(base);
        txn.setUser(user);
        txn.setDetails(req.getDetails());
        return transactionRepo.save(txn);
    }

    @Transactional
    public Transaction recordTransfer(TransferRequest req, String authHeader) {
        Asset asset = assetRepo.findById(req.getAssetId()).orElseThrow();
        Base fromBase = baseRepo.findById(req.getFromBaseId()).orElseThrow();
        Base toBase = baseRepo.findById(req.getToBaseId()).orElseThrow();
        User user = resolveUser(authHeader);

        // Deduct from source
        Inventory fromInv = inventoryRepo.findByBaseIdAndAssetId(fromBase.getId(), asset.getId())
            .orElseThrow(() -> new RuntimeException("Insufficient inventory"));
        if (fromInv.getQuantity() < req.getQuantity()) {
            throw new RuntimeException("Not enough stock at source base");
        }
        fromInv.setQuantity(fromInv.getQuantity() - req.getQuantity());
        inventoryRepo.save(fromInv);

        // Add to destination
        Inventory toInv = inventoryRepo.findByBaseIdAndAssetId(toBase.getId(), asset.getId())
            .orElse(new Inventory());
        toInv.setBase(toBase);
        toInv.setAsset(asset);
        toInv.setQuantity((toInv.getQuantity() == null ? 0 : toInv.getQuantity()) + req.getQuantity());
        inventoryRepo.save(toInv);

        // Log transaction
        Transaction txn = new Transaction();
        txn.setType(TransactionType.TRANSFER);
        txn.setAsset(asset);
        txn.setQuantity(req.getQuantity());
        txn.setFromBase(fromBase);
        txn.setToBase(toBase);
        txn.setUser(user);
        txn.setDetails(req.getDetails());
        return transactionRepo.save(txn);
    }

    @Transactional
    public Transaction recordAssignment(AssignmentRequest req, String authHeader) {
        Asset asset = assetRepo.findById(req.getAssetId()).orElseThrow();
        Base base = baseRepo.findById(req.getBaseId()).orElseThrow();
        User user = resolveUser(authHeader);
        TransactionType type = TransactionType.valueOf(req.getType());

        // Deduct from inventory
        Inventory inv = inventoryRepo.findByBaseIdAndAssetId(base.getId(), asset.getId())
            .orElseThrow(() -> new RuntimeException("No inventory found"));
        if (inv.getQuantity() < req.getQuantity()) {
            throw new RuntimeException("Insufficient inventory");
        }
        inv.setQuantity(inv.getQuantity() - req.getQuantity());
        inventoryRepo.save(inv);

        // Log transaction
        Transaction txn = new Transaction();
        txn.setType(type);
        txn.setAsset(asset);
        txn.setQuantity(req.getQuantity());
        txn.setFromBase(base);
        txn.setUser(user);
        txn.setDetails(req.getDetails());
        return transactionRepo.save(txn);
    }

    public DashboardResponse getDashboardStats(Long baseId) {
        List<Transaction> transactions = baseId != null
            ? transactionRepo.findByFromBaseIdOrToBaseId(baseId, baseId)
            : transactionRepo.findAll();

        long purchases = transactions.stream()
            .filter(t -> t.getType() == TransactionType.PURCHASE)
            .mapToLong(Transaction::getQuantity).sum();
        long transferIn = transactions.stream()
            .filter(t -> t.getType() == TransactionType.TRANSFER && t.getToBase() != null
                && (baseId == null || t.getToBase().getId().equals(baseId)))
            .mapToLong(Transaction::getQuantity).sum();
        long transferOut = transactions.stream()
            .filter(t -> t.getType() == TransactionType.TRANSFER && t.getFromBase() != null
                && (baseId == null || t.getFromBase().getId().equals(baseId)))
            .mapToLong(Transaction::getQuantity).sum();
        long assigned = transactions.stream()
            .filter(t -> t.getType() == TransactionType.ASSIGNMENT)
            .mapToLong(Transaction::getQuantity).sum();
        long expended = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENDITURE)
            .mapToLong(Transaction::getQuantity).sum();

        long netMovement = purchases + transferIn - transferOut;
        long openingBalance = 1000L; // Baseline for demo
        long closingBalance = openingBalance + netMovement - assigned - expended;

        return new DashboardResponse(
            assetRepo.count(), purchases, transferIn, transferOut,
            assigned, expended, netMovement, Math.max(0, closingBalance)
        );
    }

    public List<Transaction> getTransactions() {
        return transactionRepo.findAll();
    }

    public List<Transaction> getTransactionsByBase(Long baseId) {
        return transactionRepo.findByFromBaseIdOrToBaseId(baseId, baseId);
    }
}