package com.example.CeylonEE.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int statusCode;
    private String message;

    private String token;
    private String role;

    private String expirationTime;

    private UserDTO user;
    private List<UserDTO> userList;

    private PackageDTO packageDTO;
    private List<PackageDTO> packageList;

    private BookingDTO booking;
    private List<BookingDTO> bookingList;

    private PaymentDTO payment;
    private List<PaymentDTO> paymentList;

    private BlogDTO blog;
    private List<BlogDTO> blogList;





}