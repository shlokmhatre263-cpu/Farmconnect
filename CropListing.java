package com.example.farmconnect.marketplace;

public class CropListing {

    private String listingId;
    private String sellerId;
    private String sellerName;
    private String cropName;
    private String cropType;
    private double pricePerKg;
    private double quantityKg;
    private String location;
    private String description;
    private long   timestamp;
    private String status;

    public CropListing() {}

    public CropListing(String sellerId, String sellerName, String cropName,
                       String cropType, double pricePerKg, double quantityKg,
                       String location, String description) {
        this.sellerId    = sellerId;
        this.sellerName  = sellerName;
        this.cropName    = cropName;
        this.cropType    = cropType;
        this.pricePerKg  = pricePerKg;
        this.quantityKg  = quantityKg;
        this.location    = location;
        this.description = description;
        this.timestamp   = System.currentTimeMillis();
        this.status      = "available";
    }

    public String getListingId()                    { return listingId; }
    public void   setListingId(String listingId)    { this.listingId = listingId; }

    public String getSellerId()                     { return sellerId; }
    public void   setSellerId(String sellerId)      { this.sellerId = sellerId; }

    public String getSellerName()                   { return sellerName; }
    public void   setSellerName(String sellerName)  { this.sellerName = sellerName; }

    public String getCropName()                     { return cropName; }
    public void   setCropName(String cropName)      { this.cropName = cropName; }

    public String getCropType()                     { return cropType; }
    public void   setCropType(String cropType)      { this.cropType = cropType; }

    public double getPricePerKg()                   { return pricePerKg; }
    public void   setPricePerKg(double pricePerKg)  { this.pricePerKg = pricePerKg; }

    public double getQuantityKg()                   { return quantityKg; }
    public void   setQuantityKg(double quantityKg)  { this.quantityKg = quantityKg; }

    public String getLocation()                     { return location; }
    public void   setLocation(String location)      { this.location = location; }

    public String getDescription()                  { return description; }
    public void   setDescription(String desc)       { this.description = desc; }

    public long   getTimestamp()                    { return timestamp; }
    public void   setTimestamp(long timestamp)      { this.timestamp = timestamp; }

    public String getStatus()                       { return status; }
    public void   setStatus(String status)          { this.status = status; }
}