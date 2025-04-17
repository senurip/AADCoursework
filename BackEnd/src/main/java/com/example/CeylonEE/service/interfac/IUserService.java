package com.example.CeylonEE.service.interfac;


import com.example.CeylonEE.dto.LoginRequest;
import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.entity.User;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);

    Response update(User user);

}
