plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.googleService)
}
//marketing@roundpay.in
android {
    namespace = "com.infotech.wishmaplus"
    compileSdk = 36
    signingConfigs {
        create("config") {
            keyAlias= "wishmaplus"
            keyPassword= "wishmapluspass"
            storeFile= file("D:/H Drive/Vishnu Roundpay Project/JKS/Wishma Plus JKS/wishmaplus.jks")
            storePassword= "wishmapluspass"
        }
    }
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.infotech.wishmaplus"
        minSdk = 24
        targetSdk = 36
        versionCode = 16
        versionName = "1.16"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("config")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    ndkVersion = "29.0.13846066 rc3"
}


dependencies {
    implementation(files("libs/mobile-ffmpeg-full-gpl-4.4.aar"))
    implementation (libs.appcompat)
    implementation (libs.material)
    implementation (libs.activity)
    implementation (libs.constraintlayout)
    implementation (libs.ncorti.slidetoact)
    implementation (libs.intuit.ssp)
    implementation (libs.intuit.sdp)
    implementation (libs.bumptech.glide)
    //implementation ("jp.wasabeef:glide-transformations:4.3.0")
    implementation (libs.retrofit)
    implementation (libs.converter.scalars)
    //implementation (libs.converter.gson)
    //implementation (libs.f0ris.sweetalert)
    implementation (libs.logging.interceptor)
    implementation (libs.androidx.swiperefreshlayout)
    implementation (libs.retrofit.adapters)
    //implementation (libs.adapter.rxjava2)
    //implementation ("com.google.android.gms:play-services-auth:21.2.0")
    //implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    //implementation (libs.adapter.rxjava2)
    implementation (libs.converter.gson)
    testImplementation (libs.junit)
    //implementation ("com.github.GrenderG:Toasty:1.5.2")
    androidTestImplementation (libs.ext.junit)
    androidTestImplementation (libs.espresso.core)
    //implementation ("com.arthenica:mobile-ffmpeg-full:4.4")
    implementation (libs.media3.exoplayer)
    implementation (libs.media3.exoplayer.dash)
    implementation (libs.media3.ui)
    implementation (project(":image_picker"))
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation (libs.androidx.credentials)
    implementation (libs.credentials.play.services.auth)
    implementation (libs.identity.googleid)

    //implementation (platform("com.google.firebase:firebase-bom:33.1.2"))

    //implementation ("com.google.firebase:firebase-auth")
    implementation (project(":sweet_alert"))
    implementation (libs.install.referral)
    implementation (libs.firebase.messaging)
    implementation (libs.payu.checkout.pro)
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("androidx.fragment:fragment:1.8.9")
}