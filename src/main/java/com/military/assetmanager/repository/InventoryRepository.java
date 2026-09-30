package com.military.assetmanager.repository;
import com.military.assetmanager.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByBaseIdAndAssetId(Long baseId, Long assetId);
    List<Inventory> findByBaseId(Long baseId);
}