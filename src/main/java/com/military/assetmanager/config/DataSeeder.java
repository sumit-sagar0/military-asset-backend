package com.military.assetmanager.config;
import com.military.assetmanager.model.*;
import com.military.assetmanager.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepo,
            BaseRepository baseRepo,
            AssetCategoryRepository categoryRepo,
            AssetRepository assetRepo,
            InventoryRepository inventoryRepo) {
        return args -> {
            if (baseRepo.count() == 0) {
                // Create Bases
                Base alpha = new Base(); alpha.setName("Alpha Base"); alpha.setLocation("North Sector");
                Base bravo = new Base(); bravo.setName("Bravo Base"); bravo.setLocation("South Sector");
                alpha = baseRepo.save(alpha);
                bravo = baseRepo.save(bravo);

                // Create Asset Categories
                AssetCategory vehicles = new AssetCategory(); vehicles.setName("Vehicles");
                AssetCategory weapons = new AssetCategory();  weapons.setName("Weapons");
                AssetCategory ammo = new AssetCategory();     ammo.setName("Ammunition");
                vehicles = categoryRepo.save(vehicles);
                weapons  = categoryRepo.save(weapons);
                ammo     = categoryRepo.save(ammo);

                // Create Assets
                Asset humvee = new Asset(); humvee.setName("Humvee");       humvee.setCategory(vehicles);
                Asset tank   = new Asset(); tank.setName("M1 Abrams");      tank.setCategory(vehicles);
                Asset m4     = new Asset(); m4.setName("M4 Carbine");       m4.setCategory(weapons);
                Asset sniper = new Asset(); sniper.setName("M24 Sniper");   sniper.setCategory(weapons);
                Asset nato   = new Asset(); nato.setName("5.56mm NATO Ammo"); nato.setCategory(ammo);
                humvee = assetRepo.save(humvee);
                tank   = assetRepo.save(tank);
                m4     = assetRepo.save(m4);
                sniper = assetRepo.save(sniper);
                nato   = assetRepo.save(nato);

                // Seed Inventory for both bases
                saveInventory(inventoryRepo, alpha, humvee, 10);
                saveInventory(inventoryRepo, alpha, m4, 150);
                saveInventory(inventoryRepo, alpha, nato, 5000);
                saveInventory(inventoryRepo, bravo, tank, 5);
                saveInventory(inventoryRepo, bravo, sniper, 20);
                saveInventory(inventoryRepo, bravo, nato, 3000);

                // Create Users
                User admin = new User();
                admin.setName("System Admin"); admin.setEmail("admin@military.gov");
                admin.setPassword("admin123"); admin.setRole(Role.ADMIN);
                userRepo.save(admin);

                User commander = new User();
                commander.setName("Alpha Commander"); commander.setEmail("alpha@military.gov");
                commander.setPassword("commander123"); commander.setRole(Role.BASE_COMMANDER);
                commander.setBase(alpha);
                userRepo.save(commander);

                User logistics = new User();
                logistics.setName("Bravo Logistics"); logistics.setEmail("bravologistics@military.gov");
                logistics.setPassword("logistics123"); logistics.setRole(Role.LOGISTICS_OFFICER);
                logistics.setBase(bravo);
                userRepo.save(logistics);

                System.out.println("✅ Military Database Initialized: 2 Bases, 5 Assets, 6 Inventories, 3 Users loaded!");
            } else {
                System.out.println("ℹ️  Database already has data — skipping seed.");
            }
        };
    }

    private void saveInventory(InventoryRepository repo, Base base, Asset asset, int qty) {
        Inventory inv = new Inventory();
        inv.setBase(base); inv.setAsset(asset); inv.setQuantity(qty);
        repo.save(inv);
    }
}