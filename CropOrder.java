package com.example.farmconnect.marketplace;

public class CropOrder {

    private String orderId;
    private String listingId;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private String cropName;
    private double quantityKg;
    private double pricePerKg;
    private double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String transactionId;
    private String deliveryAddress;
    private long   timestamp;

    public CropOrder() {}

    public CropOrder(String listingId, String buyerId, String buyerName,
                     String sellerId, String sellerName, String cropName,
                     double quantityKg, double pricePerKg,
                     String paymentMethod, String deliveryAddress) {
        this.listingId       = listingId;
        this.buyerId         = buyerId;
        this.buyerName       = buyerName;
        this.sellerId        = sellerId;
        this.sellerName      = sellerName;
        this.cropName        = cropName;
        this.quantityKg      = quantityKg;
        this.pricePerKg      = pricePerKg;
        this.totalAmount     = quantityKg * pricePerKg;
        this.paymentMethod   = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.orderStatus     = "placed";
        this.timestamp       = System.currentTimeMillis();
        this.paymentStatus   = paymentMethod.equals("Cash on Delivery") ? "pending" : "completed";
    }

    public String getOrderId()                           { return orderId; }
    public void   setOrderId(String orderId)             { this.orderId = orderId; }

    public String getListingId()                         { return listingId; }
    public void   setListingId(String listingId)         { this.listingId = listingId; }

    public String getBuyerId()                           { return buyerId; }
    public void   setBuyerId(String buyerId)             { this.buyerId = buyerId; }

    public String getBuyerName()                         { return buyerName; }
    public void   setBuyerName(String buyerName)         { this.buyerName = buyerName; }

    public String getSellerId()                          { return sellerId; }
    public void   setSellerId(String sellerId)           { this.sellerId = sellerId; }

    public String getSellerName()                        { return sellerName; }
    public void   setSellerName(String sellerName)       { this.sellerName = sellerName; }

    public String getCropName()                          { return cropName; }
    public void   setCropName(String cropName)           { this.cropName = cropName; }

    public double getQuantityKg()                        { return quantityKg; }
    public void   setQuantityKg(double quantityKg)       { this.quantityKg = quantityKg; }

    public double getPricePerKg()                        { return pricePerKg; }
    public void   setPricePerKg(double pricePerKg)       { this.pricePerKg = pricePerKg; }

    public double getTotalAmount()                       { return totalAmount; }
    public void   setTotalAmount(double totalAmount)     { this.totalAmount = totalAmount; }

    public String getPaymentMethod()                     { return paymentMethod; }
    public void   setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus()                     { return paymentStatus; }
    public void   setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus()                       { return orderStatus; }
    public void   setOrderStatus(String orderStatus)     { this.orderStatus = orderStatus; }

    public String getTransactionId()                     { return transactionId; }
    public void   setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getDeliveryAddress()                           { return deliveryAddress; }
    public void   setDeliveryAddress(String deliveryAddress)     { this.deliveryAddress = deliveryAddress; }

    public long   getTimestamp()                         { return timestamp; }
    public void   setTimestamp(long timestamp)           { this.timestamp = timestamp; }
}