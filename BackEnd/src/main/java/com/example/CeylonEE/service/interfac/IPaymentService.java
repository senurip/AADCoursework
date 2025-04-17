package com.example.CeylonEE.service.interfac;

import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.entity.Payment;

import java.util.Map;

public interface IPaymentService {
    Response initiatePayment(Long userId, Long courseId);
    void handlePaymentNotification(Map<String, String> params);
}
