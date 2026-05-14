package com.example.farmconnect.activities;

public class DiseaseReport {

    public String prediction;
    public String confidence;
    public String date;

    public DiseaseReport() {}

    public DiseaseReport(String prediction,
                         String confidence,
                         String date) {

        this.prediction = prediction;
        this.confidence = confidence;
        this.date = date;
    }
}