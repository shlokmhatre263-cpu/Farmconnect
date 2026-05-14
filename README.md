# 🌾 FarmConnect — AI-Powered Smart Farming Android Application

FarmConnect is a comprehensive Android mobile application built for Indian farmers.
It combines real-time weather advisory, AI-based crop disease detection, a farmer
community forum, government scheme information, a direct crop marketplace, and full
multilingual support in English, Hindi, and Marathi — all in one single app.

---

## 📱 Screenshots

> Add your app screenshots here

| Splash & Login | Dashboard | Weather |
|---|---|---|
| <img width="337" height="749" alt="image" src="https://github.com/user-attachments/assets/93c034a3-ae5d-40e0-a0bc-a48f05105e9a" />
 | <img width="337" height="749" alt="image" src="https://github.com/user-attachments/assets/b60df685-1e5f-4980-bea2-7df8e1ed868d" />
 | <img width="337" height="749" alt="image" src="https://github.com/user-attachments/assets/3acec690-67ef-4288-8633-ad171faaa2af" />
 |

| Disease Detection | Community | Marketplace |
|---|---|---|
| <img width="337" height="749" alt="image" src="https://github.com/user-attachments/assets/e674c6a3-f873-481a-891c-f9633b6ddf8b" />
 | <img width="337" height="749" alt="image" src="https://github.com/user-attachments/assets/04c84dfc-341f-4e52-87b4-82b2c037cc9a" />
 | <img width="337" height="749" alt="image" src="https://github.com/user-attachments/assets/21dbeb4f-8801-4277-bdc4-f270a1c3e6b4" />
 |

---

## ✨ Features

- 🌦️ **Real-Time Weather Advisory**
  - Fetches live weather using device GPS
  - Calls OpenWeatherMap API via Retrofit
  - Shows temperature, city, weather description
  - Gives farming advice based on temperature

- 🌿 **AI Crop Disease Detection**
  - On-device TensorFlow Lite model
  - Detects 23 diseases across 6 crop types
  - Works completely offline — no internet needed
  - Uses temperature scaling and dual thresholds for accuracy

- 👨‍🌾 **Farmer Community Forum**
  - Post text and images
  - Like and comment on posts
  - Real-time updates via Firebase
  - Atomic counters using Firebase transactions

- 🏛️ **Government Schemes**
  - PM-KISAN
  - PM Fasal Bima Yojana
  - PM Krishi Sinchai Yojana
  - Kisan Credit Card
  - Direct Apply Online button for each scheme

- 🛒 **Crop Marketplace**
  - Farmers list crops for sale directly
  - Browse and filter by crop type
  - 4 payment methods — UPI, Card, COD, FarmWallet
  - UUID-based transaction ID for every order
  - Order history with payment status

- 🌐 **Multilingual Support**
  - English, Hindi, Marathi
  - Every screen, button, message translated
  - Language saved across sessions

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java |
| IDE | Android Studio |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 35 |
| Compile SDK | API 36 |
| Authentication | Firebase Auth (Email + Google Sign-In) |
| Database | Firebase Realtime Database |
| Storage | Firebase Storage |
| Machine Learning | TensorFlow Lite 2.10 |
| Networking | Retrofit 2.9 + Gson Converter |
| Image Loading | Glide 4.16 |
| Weather API | OpenWeatherMap |
| UI Components | Material Design Components |
| Location | FusedLocationProviderClient |

---

## 🏗️ Project Structure
app/src/main/java/com/example/farmconnect/
│
├── activities/
│   ├── BaseActivity.java          # Parent class — handles language for all screens
│   ├── SplashActivity.java        # Launch screen with auth check
│   ├── LoginActivity.java         # Email + Google Sign-In
│   ├── RegisterActivity.java      # New user registration
│   ├── LanguageSelectionActivity  # EN / Hindi / Marathi selection
│   ├── MainActivity.java          # Dashboard with 5 feature cards
│   ├── WeatherActivity.java       # GPS + OpenWeatherMap + advisory
│   ├── DiseaseActivity.java       # TFLite crop disease detection
│   ├── DiseaseReport.java         # Model class for disease report
│   ├── SchemesActivity.java       # Government schemes list
│   ├── SchemeDetailActivity.java  # Scheme details + apply link
│   ├── SchemeAdapter.java         # RecyclerView adapter for schemes
│   ├── SchemeModel.java           # Model class for scheme
│   └── ProfileActivity.java       # User profile + logout
│
├── community/
│   ├── CommunityActivity.java     # Post, image, real-time feed
│   ├── CommunityAdapter.java      # RecyclerView adapter for posts
│   ├── CommunityPost.java         # Model class for post
│   └── CommentActivity.java       # Comments on a post
│
└── marketplace/
├── MarketplaceActivity.java   # Browse + filter listings
├── SellCropActivity.java      # Create crop listing
├── BuyCropActivity.java       # Buy a crop listing
├── PaymentActivity.java       # 4 payment methods
├── MyOrdersActivity.java      # Order history
├── CropListing.java           # Model class for listing
├── CropOrder.java             # Model class for order
└── CropListingAdapter.java    # RecyclerView adapter

---

## 🧠 How Disease Detection Works
Load TFLite model from assets/main_disease_model.tflite
User picks image from Camera or Gallery
Resize image to 224 × 224 pixels
Extract RGB values of each pixel, divide by 255 (normalize)
Pack values into ByteBuffer
Run interpreter.run(byteBuffer, output)
Apply Temperature Scaling (T = 4.0) for better confidence
Check thresholds:

Confidence must be ≥ 75%
Gap between top 2 predictions must be ≥ 20%


Show disease name + description in selected language
Save result to Firebase DiseaseReports

### 🌿 Supported Crops and Diseases

| Crop | Diseases Detected |
|---|---|
| Bell Pepper | Bacterial Spot, Healthy |
| Corn | Blight, Common Rust, Gray Leaf Spot, Healthy |
| Cotton | Diseased Leaf, Diseased Plant, Fresh Leaf, Fresh Plant |
| Potato | Early Blight, Late Blight, Healthy |
| Tomato | Bacterial Spot, Early Blight, Late Blight, Target Spot, Healthy |
| Wheat | Blackpoint, Fusarium Foot Rot, Leaf Blight, Wheat Blast, Healthy Leaf |

---

## 🔥 Firebase Database Structure
Firebase Realtime Database
│
├── Users/
│   └── {uid}/
│       ├── name: "Shlok Mhatre"
│       └── email: "shlok@gmail.com"
│
├── CommunityPosts/
│   └── {postId}/
│       ├── userName, message, date
│       ├── imageBase64 (nullable)
│       ├── likes: 0
│       ├── comments: 0
│       └── commentsList/
│           └── {commentId}: "comment text"
│
├── DiseaseReports/
│   └── {reportId}/
│       ├── prediction: "tomato_late_blight"
│       ├── confidence: "87.45%"
│       └── date: "15-04-2025"
│
├── CropListings/
│   └── {listingId}/
│       ├── sellerId, sellerName
│       ├── cropName, cropType
│       ├── pricePerKg, quantityKg
│       ├── location, description
│       ├── timestamp
│       └── status: "available" / "sold"
│
└── CropOrders/
└── {orderId}/
├── listingId, buyerId, sellerId
├── buyerName, sellerName
├── cropName, quantityKg
├── pricePerKg, totalAmount
├── paymentMethod, paymentStatus
├── orderStatus: "placed"
├── transactionId (UUID)
├── deliveryAddress
└── timestamp
---

## ⚙️ Setup and Installation

### Prerequisites
- Android Studio (latest stable version)
- Android device or emulator running API 24+
- Firebase account
- OpenWeatherMap API key (free tier)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/shlokmhatre263-cpu/FarmConnect.git
cd FarmConnect
```

**2. Open in Android Studio**
File → Open → Select the FarmConnect folder

**3. Connect Firebase**
- Go to [Firebase Console](https://console.firebase.google.com)
- Create a new project
- Add Android app with package name `com.example.farmconnect`
- Download `google-services.json`
- Place it in the `app/` folder

**4. Enable Firebase Services**
Firebase Console:
✅ Authentication   → Enable Email/Password and Google Sign-In
✅ Realtime Database → Create database, set rules to authenticated access
✅ Storage          → Enable Firebase Storage

**5. Add your OpenWeatherMap API Key**

Open `WeatherActivity.java` and replace:
```java
String API_KEY = "your_api_key_here";
```
Get free API key from [openweathermap.org](https://openweathermap.org/api)

**6. Add TFLite Model**

Place these two files in `app/src/main/assets/`:
main_disease_model.tflite
main_disease_labels.txt

**7. Run the app**
Click Run ▶️ in Android Studio
Select your device or emulator
---

## 📦 Dependencies

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-database")
implementation("com.google.firebase:firebase-storage")

// Google Sign-In
implementation("com.google.android.gms:play-services-auth:21.0.0")

// Location
implementation("com.google.android.gms:play-services-location:21.0.1")

// TensorFlow Lite
implementation("org.tensorflow:tensorflow-lite:2.10.0")
implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.10.0")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Glide
implementation("com.github.bumptech.glide:glide:4.16.0")

// Material Design
implementation("com.google.android.material:material:1.11.0")

// CardView
implementation("androidx.cardview:cardview:1.0.0")
```

---

## 🌐 Multilingual Support

| Language | Code | Resource Folder |
|---|---|---|
| English | en | res/values/ |
| Hindi | hi | res/values-hi/ |
| Marathi | mr | res/values-mr/ |

Language is saved in SharedPreferences (`App_Lang` key) and applied in `BaseActivity` before every screen loads.

---

## 👥 Team Members

| Name | Role |
|---|---|
| Shlok Mhatre | Team Lead — Authentication, Language, Base Architecture |
| Meet Suryarao | Weather Module, Dashboard, Government Schemes |
| Parth Thakare | AI Disease Detection, TFLite Integration |
| Geet Patil | Community Forum, Marketplace, Payment |

---

## 📄 License
MIT License
Copyright (c) 2025 FarmConnect Team
Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

---

## 🙏 Acknowledgements

- [PlantVillage Dataset](https://plantvillage.psu.edu) — for crop disease training data
- [OpenWeatherMap](https://openweathermap.org) — for weather API
- [Firebase](https://firebase.google.com) — for backend services
- [TensorFlow Lite](https://www.tensorflow.org/lite) — for on-device ML
- [Retrofit](https://square.github.io/retrofit) — for API calls

---

⭐ **If you found this project helpful, please give it a star!**
