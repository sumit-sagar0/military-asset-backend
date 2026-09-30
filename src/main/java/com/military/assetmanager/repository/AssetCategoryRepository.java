package com.military.assetmanager.repository;
import com.military.assetmanager.model.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {}