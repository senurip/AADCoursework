package com.example.CeylonEE.service.interfac;

import com.example.CeylonEE.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface IPackgeService {

    Response addPackge(MultipartFile file, String title, String description, BigDecimal price, String duration);
    Response getAllPackges();
    Response getPackgeById(Long id);
    Response updatePackge(Long id,MultipartFile file, String name, String description, BigDecimal price, String duration);
    Response deletePackge(Long id);
}
