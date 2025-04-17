package com.example.CeylonEE.repo;

import com.example.CeylonEE.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepo extends JpaRepository<Package, Long> {
}
