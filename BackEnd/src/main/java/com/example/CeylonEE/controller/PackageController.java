package com.example.CeylonEE.controller;

import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.service.interfac.IPackgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/packages")
public class PackageController {

    @Autowired
    IPackgeService packageService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addNewPackge(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "duration", required = false) String duration
    ) {

        if (file == null || file.isEmpty() || title == null || title.isBlank() || price == null || title.isBlank()) {
            Response response = new Response();
            response.setStatusCode(400);
            response.setMessage("Please provide values for all fields");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
        Response response = packageService.addPackge(file, title, description, price, duration);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllPackges() {
        Response response = packageService.getAllPackges();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/package-by-id/{packageId}")
    public ResponseEntity<Response> getPackageById(@PathVariable Long packageId) {
        Response response = packageService.getPackgeById(packageId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{packageId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updatePackage(@PathVariable Long packageId,
                                                  @RequestParam(value = "file", required = false) MultipartFile file,
                                                  @RequestParam(value = "title", required = false) String title,
                                                  @RequestParam(value = "description", required = false) String description,
                                                  @RequestParam(value = "price", required = false) BigDecimal price,
                                                  @RequestParam(value = "duration", required = false) String duration

    ) {
        Response response = packageService.updatePackge(packageId, file, title, description, price, duration);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{packageId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deletePackage(@PathVariable Long packageId) {
        Response response = packageService.deletePackge(packageId);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }


}
