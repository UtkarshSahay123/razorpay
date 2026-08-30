package com.eduflow.backend.dto;

public class PaymentOrderResponseDto {
    private String orderId;
    private String keyId; // So the frontend knows which key to use
    private int amount; // in paise

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
