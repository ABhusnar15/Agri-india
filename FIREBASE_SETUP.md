# 🍏 Firebase Setup Guide for AgriIndiaApp

This project comes pre-configured with **Firebase Authentication** and **Cloud Firestore** SDKs with a seamless **offline Room database fallback**. 

Follow these steps when you are ready to connect the app to your live Firebase Console project.

---

## Step 1: Create a Project in Firebase Console

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add project** (or **Create a project**).
3. Name your project (e.g., `AgriIndiaApp`).
4. Choose whether to enable Google Analytics, then click **Create project**.

---

## Step 2: Register the Android App

1. In your Firebase project overview, click the **Android icon** (`+ Add app`).
2. Enter the exact Android package name:
   ```text
   com.agriindia.app
   ```
3. Enter an optional App nickname (e.g., `AgriIndia Android`).
4. (Optional) Add your Debug SHA-1 fingerprint (required if enabling Phone OTP or Google Sign-In):
   * Run in terminal: `./gradlew signingReport`
   * Copy the `SHA1` fingerprint under `debug` build variant into Firebase Console.
5. Click **Register app**.

---

## Step 3: Download and Replace `google-services.json`

1. Download the generated `google-services.json` file.
2. Replace the placeholder file located at:
   ```text
   AgriIndiaApp/app/google-services.json
   ```
3. Make sure the file contains your real `project_id`, `mobilesdk_app_id`, and `api_key`.

---

## Step 4: Enable Authentication Methods

In the Firebase Console sidebar:
1. Navigate to **Build > Authentication**.
2. Click **Get Started**.
3. Under the **Sign-in method** tab:
   - **Email/Password**: Enable and click **Save**.
   - **Phone**: Enable if you plan to use SMS OTP verification for farmers.

---

## Step 5: Enable Cloud Firestore Database

1. Navigate to **Build > Firestore Database**.
2. Click **Create Database**.
3. Select your preferred database location (e.g., `asia-south1` for India).
4. Start in **Test mode** (or set appropriate read/write rules):
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       // Allow read access to Mandi prices & community posts for all users
       match /mandi_prices/{docId} {
         allow read: if true;
         allow write: if request.auth != null;
       }
       match /community_posts/{docId} {
         allow read, create: if true;
         allow update, delete: if request.auth != null;
       }
     }
   }
   ```
5. Click **Enable**.

---

## Step 6: Test & Build the App

Once `google-services.json` is in place, rebuild the app:
```bash
./gradlew assembleDebug
```
When online with valid Firebase credentials, user signups, logins, community posts, and mandi price feeds will automatically sync with your live Firebase backend!
