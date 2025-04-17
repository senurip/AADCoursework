package com.example.CeylonEE.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogDTO {

    private Long id;
    private String title;
    private String content;
    private String author;
    private String imgUrl;
    private UserDTO userDTO;
}
