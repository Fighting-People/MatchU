plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.matchux"
    compileSdk = 34 // AGP 8.2.2 환경에서 가장 안정적인 타겟

    defaultConfig {
        applicationId = "com.example.matchux"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // 컴포즈 버전을 코틀린 1.9.22 수준에 맞춤
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // UI 및 네비게이션 표준 연결
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // 컴포즈 핵심 요소 수동 지정
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.ui:ui-graphics:1.6.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.compose.bom))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

// 🔥 라이브러리 내부에서 멋대로 최신 버전(35, 36 요구용)을 끌고 오지 못하도록 완전히 결계를 칩니다.
configurations.all {
    resolutionStrategy {
        force("androidx.core:core:1.12.0")
        force("androidx.core:core-ktx:1.12.0")
        force("androidx.activity:activity:1.8.2")
        force("androidx.activity:activity-ktx:1.8.2")

        force("androidx.lifecycle:lifecycle-runtime-android:2.7.0")
        force("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
        force("androidx.lifecycle:lifecycle-viewmodel-android:2.7.0")
        force("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

        force("androidx.savedstate:savedstate-android:1.2.1")
        force("androidx.savedstate:savedstate-ktx:1.2.1")
    }
}