package com.lcwd.store.controller;


import com.lcwd.store.dtos.PhonepayPayload;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.payments.v1.PaymentTransactionClient;
import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
import com.phonepe.sdk.pg.payments.v1.models.request.PgPayRequest;
import com.phonepe.sdk.pg.payments.v1.models.response.PayPageInstrumentResponse;
import com.phonepe.sdk.pg.payments.v1.models.response.PgPayResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@RestController
class PhonepayController {
    private static final String SALT_KEY = "2b9eaec5-6c6b-4928-9267-4a3cea66d770";
    private static final String SALT_INDEX = "1";

    String merchantId = "M22L0ONMXEH8Z";
    String saltKey = "2b9eaec5-6c6b-4928-9267-4a3cea66d770";
    Integer saltIndex = 1;
    Env env = Env.PROD;

    boolean shouldPublishEvents = true;
    PhonePePaymentClient phonepeClient = new PhonePePaymentClient(merchantId, saltKey, saltIndex, env, shouldPublishEvents);


    @PostMapping("/calculateHash")
    public ResponseEntity<String> calculateHash(@RequestBody PhonepayPayload phonepayPayload) {

        String merchantId = "M22L0ONMXEH8Z";
        String merchantTransactionId = UUID.randomUUID().toString().substring(0, 34);
        long amount = phonepayPayload.getAmount()*100;
        String merchantUserId = "vaayuskylink@gmail.com";
        String callbackurl = "https://www.vaayuskylink.com";

//        PgPayRequest pgPayRequest = PgPayRequest.PayPagePayRequestBuilder()
//                .amount(amount)
//                .merchantId(merchantId)
//                .merchantTransactionId(merchantTransactionId)
//                .callbackUrl(callbackurl)
//                .merchantUserId(merchantUserId)
//                .build();
//
//        PhonePeResponse<PgPayResponse> payResponse = phonepeClient.pay(pgPayRequest);
//        PayPageInstrumentResponse payPageInstrumentResponse = (PayPageInstrumentResponse) payResponse.getData().getInstrumentResponse();
//        String url = payPageInstrumentResponse.getRedirectInfo().getUrl();


        String redirecturl="https://www.vaayuskylink.com";
        String redirectMode="REDIRECT";

        PgPayRequest pgPayRequest=PgPayRequest.PayPagePayRequestBuilder()
                .amount(amount)
                .merchantId(merchantId)
                .merchantTransactionId(merchantTransactionId)
                .callbackUrl(callbackurl)
                .merchantUserId(merchantUserId)
                .redirectUrl(redirecturl)
                .redirectMode(redirectMode)
                .build();

        PhonePeResponse<PgPayResponse> payResponse=phonepeClient.pay(pgPayRequest);
        PayPageInstrumentResponse payPageInstrumentResponse=(PayPageInstrumentResponse)payResponse.getData().getInstrumentResponse();
        String url=payPageInstrumentResponse.getRedirectInfo().getUrl();


return  ResponseEntity.ok(url);
    }
    private PaymentRequest createPaymentRequest() {
        // Initialize PaymentRequest with the provided values
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantId("M22L0ONMXEH8Z");
        String merchantTransactionId = UUID.randomUUID().toString().substring(0,34);
        paymentRequest.setMerchantTransactionId(merchantTransactionId);
        paymentRequest.setMerchantUserId("MU933037302229373");
        paymentRequest.setAmount(1.0);
        paymentRequest.setCallbackUrl("http://http://vaayuskylink.com");
        paymentRequest.setMobileNumber("9056624920");

        DeviceContext deviceContext = new DeviceContext();
        deviceContext.setDeviceOS("WEB");
        paymentRequest.setDeviceContext(deviceContext);

        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setType("UPI_INTENT");
        paymentInstrument.setTargetApp("com.phonepe.app");
        paymentRequest.setPaymentInstrument(paymentInstrument);

        return paymentRequest;
    }
    private String calculateSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes).toLowerCase();
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            hexString.append(String.format("%02X", b & 0xFF));
        }
        return hexString.toString();
    }
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class PaymentRequest {
    private String merchantId;
    private String merchantTransactionId;
    private String merchantUserId;
    private double amount;
    private String callbackUrl;
    private String mobileNumber;
    private DeviceContext deviceContext;
    private PaymentInstrument paymentInstrument;

    // getters and setters

    @Override
    public String toString() {
        return "{" +
                "\"merchantId\":\"" + merchantId + '\"' +
                ", \"merchantTransactionId\":\"" + merchantTransactionId + '\"' +
                ", \"merchantUserId\":\"" + merchantUserId + '\"' +
                ", \"amount\":" + amount +
                ", \"callbackUrl\":\"" + callbackUrl + '\"' +
                ", \"mobileNumber\":\"" + mobileNumber + '\"' +
                ", \"deviceContext\":" + deviceContext +
                ", \"paymentInstrument\":" + paymentInstrument +
                '}';
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class DeviceContext {
    private String deviceOS;

    // getters and setters
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class PaymentInstrument {
    private String type;
    private String targetApp;

    // getters and setters
}