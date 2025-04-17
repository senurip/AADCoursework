package com.example.CeylonEE.repo;

import com.example.CeylonEE.entity.Booking;
import com.example.CeylonEE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BookingRepo extends JpaRepository<Booking, Long> {

    @Query("SELECT count(b) FROM Booking b WHERE b.customer.id = :customerId AND b.pkg.id = :packageId")
    int countByCustomerIdAndPkgId(Long customerId, Long packageId);

    boolean existsByCustomerIdAndPkgId(long customerId, long packageId);

    List<Booking> findAllByCustomer(User user);
}
