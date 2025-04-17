package com.example.CeylonEE.controller;

import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.service.impl.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Response> getAllBookings() {
        Response response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getAllBookingsByUserId/{userId}")
    public ResponseEntity<Response> getAllBookingsByUserId(@PathVariable Long userId) {
        Response response = bookingService.getAllBookingsByUserId(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/countByCustomerIdAndPkgId")
    public int countByCustomerIdAndPkgId(@RequestParam Long customerId, @RequestParam Long packageId) {
        return bookingService.countByCustomerIdAndPkgId(customerId, packageId);
    }


    @GetMapping("/packages-with-counts/{userId}")
    public ResponseEntity<Response> getPackagesWithPurchaseCounts(@PathVariable Long userId) {
        Response response = bookingService.getPackagesWithPurchaseCounts(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}