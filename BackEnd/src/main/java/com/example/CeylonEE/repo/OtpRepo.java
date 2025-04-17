package com.example.CeylonEE.repo;

import com.example.CeylonEE.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtpRepo extends JpaRepository<Otp, Long> {
    List<Otp> findAllByEmail(String email);
}
