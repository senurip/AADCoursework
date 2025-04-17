package com.example.CeylonEE.service.impl;

import com.example.CeylonEE.dto.PaymentDTO;
import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.entity.*;
import com.example.CeylonEE.entity.Package;
import com.example.CeylonEE.exception.OurException;
import com.example.CeylonEE.repo.BookingRepo;
import com.example.CeylonEE.repo.PackageRepo;
import com.example.CeylonEE.repo.PaymentRepo;
import com.example.CeylonEE.repo.UserRepo;
import com.example.CeylonEE.service.interfac.IPaymentService;
import com.example.CeylonEE.utils.PayHereHashUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService implements IPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PackageRepo packageRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    // Use a more descriptive name
    private final Map<String, PaymentDetails> pendingPayments = new HashMap<>();

    private static class PaymentDetails {
        Long userId;
        Long pkgId;
        Double amount;
        String currency;
        String orderId;

        PaymentDetails(Long userId, Long pkgId, Double amount, String currency, String orderId) {
            this.userId = userId;
            this.pkgId = pkgId;
            this.amount = amount;
            this.currency = currency;
            this.orderId = orderId;
        }
    }

    @Override
    public Response initiatePayment(Long userId, Long pkgId) {
        Response response = new Response();

        try {
            // Fetch user and package
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new OurException("User not found"));
            Package pkg = packageRepo.findById(pkgId)
                    .orElseThrow(() -> new OurException("Package not found"));

            // Generate unique order ID
            String orderId = userId + "-" + UUID.randomUUID();  // Include user ID
            logger.info("Initiating payment for user {} and package {}. Order ID: {}", userId, pkgId, orderId);

            // Store payment details
            Double amount = pkg.getPrice().doubleValue();
            String currency = "LKR";
            pendingPayments.put(orderId, new PaymentDetails(userId, pkgId, amount, currency, orderId));
            logger.debug("Payment details stored for order ID: {}", orderId); // Use debug for non-critical info

            // Generate PayHere hash
            DecimalFormat df = new DecimalFormat("0.00");
            String amountFormatted = df.format(amount);
            String hashInput = merchantId + orderId + amountFormatted + currency +
                    PayHereHashUtil.getMd5(merchantSecret);
            logger.debug("Hash input: {}", hashInput);  //  Use debug
            String hash = PayHereHashUtil.getMd5(hashInput);
            logger.info("Generated PayHere hash: {}", hash);

            // Create PaymentDTO
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setAmount(amount);
            paymentDTO.setCurrency(currency);
            paymentDTO.setOrderId(orderId);
            paymentDTO.setHash(hash);
            paymentDTO.setMerchantId(merchantId);

            response.setStatusCode(200);
            response.setMessage("Payment initiation successful. Redirecting to PayHere..."); // More user-friendly
            response.setPayment(paymentDTO);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            logger.error("Failed to initiate payment: {}", e.getMessage()); // Include context
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to initiate payment. Please try again."); // Generic user message
            logger.error("Error initiating payment: {}", e.getMessage(), e);
        }
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handlePaymentNotification(Map<String, String> params) {
        try {
            logger.info("Received PayHere payment notification: {}", params);

            String merchantId = params.get("merchant_id");
            String orderId = params.get("order_id");
            String payhereAmount = params.get("payhere_amount");
            String payhereCurrency = params.get("payhere_currency");
            String statusCode = params.get("status_code");
            String md5sig = params.get("md5sig");
            String paymentId = params.get("payment_id");

            // Check for pending payment
            PaymentDetails pendingData = pendingPayments.get(orderId);
            if (pendingData == null) {
                logger.warn("Received notification for unknown/already processed order ID: {}", orderId);
                return;
            }

            // Verify hash
            String localMd5sig = PayHereHashUtil.getMd5(
                    merchantId + orderId + payhereAmount + payhereCurrency +
                            statusCode + PayHereHashUtil.getMd5(merchantSecret)
            );
            logger.debug("Calculated hash: {}, Received hash: {}", localMd5sig, md5sig);

            if (localMd5sig.equalsIgnoreCase(md5sig) && "2".equals(statusCode)) {
                // Payment successful
                logger.info("Payment successful for order ID: {}", orderId);

                User user = userRepo.findById(pendingData.userId)
                        .orElseThrow(() -> new OurException("User not found"));  //Should not happen
                Package pkg = packageRepo.findById(pendingData.pkgId)
                        .orElseThrow(() -> new OurException("Package not found")); //Should not happen

//                if (bookingRepo.existsByCustomerIdAndPkgId(pendingData.userId, pendingData.pkgId)) {
//                    String message = "Booking already exists for user " + pendingData.userId + " and package " + pendingData.pkgId;
//                    logger.warn(message);
//                    throw new OurException(message);
//                }

                // Create booking
                Booking booking = new Booking();
                booking.setCustomer(user);
                booking.setPkg(pkg);
                bookingRepo.save(booking);
                logger.info("Booking created with ID: {} for order ID: {}", booking.getBookingId(), orderId);

                // Create payment
                Payment payment = new Payment();
                payment.setAmount(pendingData.amount);
                payment.setCurrency(pendingData.currency);
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setBooking(booking);
                payment.setOrderId(orderId);
                payment.setPaymentId(paymentId);
                payment.setHash(localMd5sig);
                paymentRepo.save(payment);
                logger.info("Payment recorded with ID: {} and status COMPLETED for order ID: {}", payment.getPaymentId(), orderId);

                pendingPayments.remove(orderId);
                logger.debug("Removed order ID {} from pending payments", orderId);
            } else {
                // Payment failed
                logger.warn("Payment failed for order ID: {}. Status code: {}, Hash match: {}", orderId, statusCode, localMd5sig.equalsIgnoreCase(md5sig));
                pendingPayments.remove(orderId);
                logger.debug("Removed order ID {} from pending payments due to payment failure", orderId);
                //  No need to throw an exception here, the method should complete.
            }
        } catch (OurException e) {
            logger.error("OurException occurred while handling payment notification: {}", e.getMessage());
            throw new OurException("Failed to handle payment notification: " + e.getMessage()); // Wrap for clarity
        } catch (Exception e) {
            logger.error("Error processing payment notification: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to handle payment notification: " + e.getMessage(), e);
        }
    }
}

