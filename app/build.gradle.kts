plugins {
    id("com.android.application")
<<<<<<< HEAD
    id("org.jetbrains.kotlin.android")
    // Firebase 파일이 없을 때 오류를 방지하기 위해 아래 줄을 주석 처리했습니다.
    // id("com.google.gms.google-services") version "4.4.0"
=======
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
>>>>>>> develop
}

android {
    namespace = "com.example.matchux"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.matchux"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
<<<<<<< HEAD
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
=======
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


>>>>>>> develop
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
<<<<<<< HEAD
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.core:core-ktx:1.12.0")

    // Firebase를 사용하지 않고 UI만 확인할 때는 아래 라이브러리들도 주석 처리하면 더 안전합니다.
    /*
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    */

    implementation("androidx.datastore:datastore-preferences:1.0.0")
=======

    // 리스트 디자인을 위한 카드뷰 라이브러리
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase 관련
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
>>>>>>> develop

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
<<<<<<< HEAD
=======

    implementation("com.google.android.flexbox:flexbox:3.0.0")
>>>>>>> develop
}