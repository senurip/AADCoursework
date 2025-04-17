package com.example.CeylonEE.service.interfac;

import com.example.CeylonEE.dto.Response;

public interface IBookingService {
   Response getAllBookings();
   Response getAllBookingsByUserId(Long userId);
   boolean existsByCustomerIdAndPackageId(Long customerId, Long packageId);
   int countByCustomerIdAndPkgId(Long customerId, Long packageId);
   Response getPackagesWithPurchaseCounts(Long userId);
}
