plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.chihun.chatbot"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.chihun.chatbot"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    // ✅ BuildConfig 활성화
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // 소스 코드 컴파일 시 Java 11을 사용하도록 설정
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Kotlin 컴파일 시 JVM target을 11로 설정
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // 📌 최신 Material3 디자인 시스템 사용
    implementation("com.google.android.material:material:1.9.0")

    // 📌 Material3 사용 시 필요한 Activity 호환성 라이브러리
    implementation("androidx.activity:activity:1.7.0")
    implementation("androidx.activity:activity-ktx:1.7.0")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")

    // MVVM 핵심
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.4.0")

    // Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // (선택) Temi SDK 및 Gemini 연동 라이브러리는 추후 추가
}
