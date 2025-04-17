package com.example.CeylonEE.controller;

import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.service.interfac.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/blogs")
public class BlogController {

    @Autowired
    private IBlogService blogService;

    @PostMapping(value = "/add/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> addBlog(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("author") String author) {
        Response response = blogService.addBlog(userId, file, title, content, author);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllBlogs() {
        Response response = blogService.getAllBlogs();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getBlogById/{id}")
    public ResponseEntity<Response> getBlogById(@PathVariable Long id) {
        Response response = blogService.getBlogById(id);
        if (response.getStatusCode() == 200) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> updateBlog(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "author", required = false) String author) {
        Response response = blogService.updateblog(id, file, title, content, author);
        if (response.getStatusCode() == 200) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (response.getStatusCode() == 404) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteBlog(@PathVariable Long id) {
        Response response = blogService.deleteBlog(id);
        if (response.getStatusCode() == 200) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (response.getStatusCode() == 404) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getBlogsByUserId/{userId}")
    public ResponseEntity<Response> getBlogsByUserId(@PathVariable Long userId) {
        Response response = blogService.getBlogsByUserId(userId);
        if (response.getStatusCode() == 200) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (response.getStatusCode() == 404) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}