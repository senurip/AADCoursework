package com.example.CeylonEE.service.impl;

import com.example.CeylonEE.dto.PackageDTO;
import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.entity.Package;
import com.example.CeylonEE.exception.OurException;
import com.example.CeylonEE.repo.PackageRepo;
import com.example.CeylonEE.service.interfac.IPackgeService;
import com.example.CeylonEE.utils.FileUploadUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class PackgeService implements IPackgeService {

    @Autowired
    PackageRepo packageRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Response addPackge(MultipartFile file, String title, String description, BigDecimal price, String duration) {
        Response response = new Response();
        String imgUrl = "";
        String uploadDir = "ImagesFolder";

        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            System.out.println(fileName);
            imgUrl = FileUploadUtil.saveFile(uploadDir, fileName, file);

            PackageDTO packageDTO = new PackageDTO();
            packageDTO.setTitle(title);
            packageDTO.setDescription(description);
            packageDTO.setImgUrl(imgUrl);
            packageDTO.setPrice(price);
            packageDTO.setDuration(duration);
            packageRepo.save(modelMapper.map(packageDTO, Package.class));
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setPackageDTO(packageDTO);
        } catch (IOException e) {
            response.setStatusCode(500);
            response.setMessage("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding a course: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllPackges() {
        Response response = new Response();
        try {
            List<Package> packageList = packageRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<PackageDTO> packageDTOList = modelMapper.map(packageList, new TypeToken<List<PackageDTO>>() {}.getType());
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setPackageList(packageDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving packages: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getPackgeById(Long id) {
        Response response = new Response();

        try {
            Package aPackage = packageRepo.findById(id).orElseThrow(() -> new OurException("Package Not Found"));
            PackageDTO packageDTO = modelMapper.map(aPackage, PackageDTO.class);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setPackageDTO(packageDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error get packageDetails " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updatePackge(Long id, MultipartFile file, String title, String description, BigDecimal price, String duration) {
        Response response = new Response();
        String uploadDir = "ImagesFolder";

        try {
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                imageUrl = FileUploadUtil.saveFile(uploadDir, fileName, file);
            }

            Package aPackage = packageRepo.findById(id).orElseThrow(() -> new OurException("Package Not Found"));
            if (title != null) aPackage.setTitle(title);
            if (price != null) aPackage.setPrice(price);
            if (description != null) aPackage.setDescription(description);
            if (duration != null) aPackage.setDuration(duration);
            if (imageUrl != null) aPackage.setImgUrl(imageUrl);

            Package updatedPackage = packageRepo.save(aPackage);
            PackageDTO packageDTO = modelMapper.map(updatedPackage, PackageDTO.class);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setPackageDTO(packageDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating a course " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deletePackge(Long id) {
        Response response = new Response();

        try {
            packageRepo.findById(id).orElseThrow(() -> new OurException("Package Not Found"));
            packageRepo.deleteById(id);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error delecting a packagee " + e.getMessage());
        }
        return response;
    }


}