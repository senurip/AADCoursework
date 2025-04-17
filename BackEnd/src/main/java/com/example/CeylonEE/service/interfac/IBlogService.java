package com.example.CeylonEE.service.interfac;

import com.example.CeylonEE.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface IBlogService {

    Response addBlog(Long userId,MultipartFile file, String title, String content, String author);
    Response getAllBlogs();
    Response getBlogById(Long id);
    Response updateblog(Long id,MultipartFile file,String title,String content,String author);
    Response deleteBlog(Long id);
    Response getBlogsByUserId(Long userId);
}
