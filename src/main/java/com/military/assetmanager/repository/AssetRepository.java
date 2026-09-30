package com.military.assetmanager.repository;
import com.military.assetmanager.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AssetRepository extends JpaRepository<Asset, Long> {}