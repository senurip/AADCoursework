package com.example.CeylonEE.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static String saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get("C:\\Users\\ASUS\\Desktop\\CeylonEE\\Ceylon_EE_Project\\src\\main\\resources\\static\\"+uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String imgUrl = "";

        try(InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate the image URL relative to the static directory
            imgUrl = "/" + uploadDir + "/" + fileName;
        } catch (IOException ioException) {
            System.out.printf("Error saving file: %s\n", ioException.getMessage());
            throw ioException; // Re-throw to let the caller handle it
        }

        return imgUrl;
    }
}