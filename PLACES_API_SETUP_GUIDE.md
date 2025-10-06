# Places API (New) Setup Guide

## Overview
This guide explains the changes made to use the **new Google Places API** instead of the deprecated legacy API, and the manual steps you need to complete.

---

## What Was Changed

### 1. **Dependencies Added** (`app/build.gradle.kts`)
- **OkHttp 4.12.0**: For making HTTP requests to the Places API
- **Gson 2.10.1**: For parsing JSON responses from the API

```kotlin
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.google.code.gson:gson:2.10.1")
```

### 2. **Code Changes** (`NearbyGroceriesActivity.java`)
- Removed deprecated `findCurrentPlace()` API calls
- Implemented HTTP-based requests to the new Places API endpoint
- Added `searchNearbyGroceriesWithLocation()` method that:
  - Builds JSON request with location and radius
  - Makes async HTTP POST request to `https://places.googleapis.com/v1/places:searchNearby`
  - Parses JSON response and adds markers to the map
  - Searches within 5km radius for grocery stores and supermarkets

---

## Manual Steps Required

### Step 1: Enable the New Places API in Google Cloud Console

🚨 **CRITICAL**: You must enable the new API in Google Cloud Console, otherwise you'll get authentication errors.

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project (or create one if you haven't)
3. Navigate to **APIs & Services** → **Library**
4. Search for **"Places API (New)"** 
   - ⚠️ Make sure it says **(New)** - don't confuse with the legacy "Places API"
5. Click on **"Places API (New)"**
6. Click the **"ENABLE"** button
7. Wait for it to be enabled (may take a few seconds)

### Step 2: Verify Your API Key Has Access

1. In Google Cloud Console, go to **APIs & Services** → **Credentials**
2. Find your API key: `AIzaSyAngBD2Z_mqn4dB3qmwv-3igqr7mgb1fvY`
3. Click on the API key to edit it
4. Under **API restrictions**, ensure either:
   - **Don't restrict key** is selected, OR
   - **Restrict key** is selected and **"Places API (New)"** is in the allowed list
5. Click **Save**

### Step 3: Set Up Billing (If Not Already Done)

⚠️ The new Places API requires a billing account to be linked to your project.

1. In Google Cloud Console, go to **Billing**
2. Link a billing account (Google provides $200 free credit monthly)
3. The Places API (New) has the following pricing:
   - **Nearby Search**: $32.00 per 1,000 requests
   - You get $200 free credits per month (≈6,250 free searches/month)

### Step 4: Test the Implementation

1. **Sync Gradle**: In Android Studio, click **File** → **Sync Project with Gradle Files**
2. **Clean and Rebuild**: Click **Build** → **Clean Project**, then **Build** → **Rebuild Project**
3. **Run the app** on a device or emulator
4. Navigate to the **Nearby Groceries** screen
5. Click the location button
6. You should see markers appear for nearby grocery stores

### Step 5: Monitor API Usage (Recommended)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Navigate to **APIs & Services** → **Dashboard**
3. Click on **"Places API (New)"**
4. Monitor your usage to avoid unexpected charges

---

## Troubleshooting

### Error: "API_KEY_INVALID" or "API_NOT_ACTIVATED"
- **Solution**: Complete Steps 1 and 2 above to enable the API and configure your key

### Error: "BILLING_NOT_ENABLED"
- **Solution**: Complete Step 3 to set up billing

### No grocery stores appear on the map
- **Check Logcat** for error messages (filter by "NearbyGroceries")
- Verify location permissions are granted
- Ensure you have internet connectivity
- Try increasing the search radius in the code (currently 5000 meters)

### App crashes on startup
- Make sure you've synced Gradle dependencies
- Clean and rebuild the project
- Check that OkHttp and Gson dependencies were downloaded successfully

---

## Code Architecture

### How It Works

1. **User clicks location button** → `checkLocationPermissionAndGetLocation()`
2. **Get current location** → `getCurrentLocationAndSearch()`
3. **Call search method** → `searchNearbyGroceries()`
4. **Get location coordinates** → `searchNearbyGroceriesWithLocation(lat, lng)`
5. **Build JSON request** with:
   - Included types: `grocery_store`, `supermarket`
   - Location restriction: Circle with 5km radius
   - Max results: 20 places
6. **Make HTTP POST** to Places API (New)
7. **Parse JSON response** and extract place names and coordinates
8. **Add markers** to Google Map

### API Request Format

```json
{
  "includedTypes": ["grocery_store", "supermarket"],
  "locationRestriction": {
    "circle": {
      "center": {
        "latLng": {
          "latitude": 37.7749,
          "longitude": -122.4194
        }
      },
      "radius": 5000.0
    }
  },
  "maxResultCount": 20
}
```

### API Response Format

```json
{
  "places": [
    {
      "displayName": {
        "text": "Whole Foods Market"
      },
      "location": {
        "latitude": 37.7751,
        "longitude": -122.4195
      }
    }
  ]
}
```

---

## Security Recommendations

### 1. Move API Key to local.properties (More Secure)

Currently, the API key is hardcoded. For better security:

**Add to `local.properties`:**
```properties
MAPS_API_KEY=AIzaSyAngBD2Z_mqn4dB3qmwv-3igqr7mgb1fvY
```

**Update `app/build.gradle.kts`:**
```kotlin
android {
    defaultConfig {
        // Read from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val apiKey = properties.getProperty("MAPS_API_KEY", "")
        buildConfigField("String", "MAPS_API_KEY", "\"$apiKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = apiKey
    }
    
    buildFeatures {
        buildConfig = true
    }
}
```

**Update `NearbyGroceriesActivity.java`:**
```java
private static final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;
```

### 2. Add API Key Restrictions in Google Cloud Console

1. Go to your API key settings
2. Under **Application restrictions**, select **Android apps**
3. Add your app's package name and SHA-1 certificate fingerprint
4. This prevents unauthorized use of your API key

---

## Cost Optimization Tips

1. **Cache results**: Store search results to avoid repeated API calls
2. **Limit search radius**: Reduce from 5km to 2-3km if appropriate
3. **Reduce max results**: Lower from 20 to 10 places
4. **Implement debouncing**: Don't search on every location update
5. **Use place IDs**: Store place IDs and only fetch details when needed

---

## Additional Resources

- [Places API (New) Documentation](https://developers.google.com/maps/documentation/places/web-service/op-overview)
- [Nearby Search (New) API Reference](https://developers.google.com/maps/documentation/places/web-service/nearby-search)
- [Google Cloud Console](https://console.cloud.google.com/)
- [Pricing Information](https://developers.google.com/maps/billing-and-pricing/pricing#places-new)

---

## Summary

✅ **Completed**:
- Added OkHttp and Gson dependencies
- Implemented HTTP-based Places API (New) integration
- Created async request/response handling
- Added error handling and logging

🔴 **You Must Do**:
1. Enable "Places API (New)" in Google Cloud Console
2. Configure API key permissions
3. Set up billing account
4. Test the implementation
5. (Optional) Improve API key security

---

**Last Updated**: October 6, 2025
