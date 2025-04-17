package com.example.CeylonEE.service.impl;

import com.example.CeylonEE.dto.BlogDTO;
import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.dto.UserDTO;
import com.example.CeylonEE.entity.Blog;
import com.example.CeylonEE.entity.User;
import com.example.CeylonEE.exception.OurException;
import com.example.CeylonEE.repo.BlogRepo;
import com.example.CeylonEE.repo.UserRepo;
import com.example.CeylonEE.service.interfac.IBlogService;
import com.example.CeylonEE.utils.FileUploadUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BlogService implements IBlogService {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Response addBlog(Long userId, MultipartFile file, String title, String content, String author) {
        Response response = new Response();
        String imgUrl = "";
        String uploadDir = "ImagesFolder";

        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            System.out.println(fileName);
            imgUrl = FileUploadUtil.saveFile(uploadDir, fileName, file);

            User user = userRepo.findById(userId).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);

            Blog blog = new Blog();
            blog.setTitle(title);
            blog.setContent(content);
            blog.setAuthor(author);
            blog.setImgUrl(imgUrl);
            blog.setUser(user); // Set the user for the blog

            Blog savedBlog = blogRepo.save(blog);
            BlogDTO blogDTO = modelMapper.map(savedBlog, BlogDTO.class);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBlog(blogDTO);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (IOException e) {
            response.setStatusCode(500);
            response.setMessage("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding a blog: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBlogs() {
        Response response = new Response();

        try {
            List<Blog> blogList = blogRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BlogDTO> blogDTOList = blogList.stream()
                    .map(blog -> {
                        BlogDTO blogDTO = modelMapper.map(blog, BlogDTO.class);
                        blogDTO.setUserDTO(modelMapper.map(blog.getUser(), UserDTO.class));
                        return blogDTO;
                    })
                    .collect(Collectors.toList());
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBlogList(blogDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving packages: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getBlogById(Long id) {
        Response response = new Response();

        try {
            Blog blog = blogRepo.findById(id).orElseThrow(() -> new OurException("Blog Not Found"));
            BlogDTO blogDTO = modelMapper.map(blog, BlogDTO.class);
            blogDTO.setUserDTO(modelMapper.map(blog.getUser(), UserDTO.class));
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBlog(blogDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error get BlogDetails " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateblog(Long id, MultipartFile file, String title, String content, String author) {
        Response response = new Response();
        String uploadDir = "ImagesFolder";

        try {
            Blog blog = blogRepo.findById(id).orElseThrow(() -> new OurException("Blog Not Found"));

            if (title != null) blog.setTitle(title);
            if (content != null) blog.setContent(content);
            if (author != null) blog.setAuthor(author);

            if (file != null && !file.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String imageUrl = FileUploadUtil.saveFile(uploadDir, fileName, file);
                blog.setImgUrl(imageUrl);
            }

            Blog updatedBlog = blogRepo.save(blog);
            BlogDTO blogDTO = modelMapper.map(updatedBlog, BlogDTO.class);
            blogDTO.setUserDTO(modelMapper.map(updatedBlog.getUser(), UserDTO.class));

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBlog(blogDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating a Blog " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteBlog(Long id) {
        Response response = new Response();

        try {
            blogRepo.findById(id).orElseThrow(() -> new OurException("Blog Not Found"));
            blogRepo.deleteById(id);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error delecting a blog " + e.getMessage());
        }
        return response;
    }

    // New method to fetch blogs by user ID
    public Response getBlogsByUserId(Long userId) {
        Response response = new Response();

        try {
            User user = userRepo.findById(userId).orElseThrow(() -> new OurException("User Not Found"));
            List<Blog> userBlogs = blogRepo.findByUser(user);
            List<BlogDTO> blogDTOList = userBlogs.stream()
                    .map(blog -> {
                        BlogDTO blogDTO = modelMapper.map(blog, BlogDTO.class);
                        blogDTO.setUserDTO(modelMapper.map(blog.getUser(), UserDTO.class));
                        return blogDTO;
                    })
                    .collect(Collectors.toList());
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBlogList(blogDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving blogs for user " + userId + ": " + e.getMessage());
        }
        return response;
    }
}