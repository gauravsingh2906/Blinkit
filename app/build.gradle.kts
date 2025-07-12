plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.android.example.blinkit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.example.blinkit"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    buildFeatures {
        viewBinding = true
    }
}


    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-database")
        implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
        implementation("com.google.firebase:firebase-storage")
        implementation("com.google.firebase:firebase-firestore")

        implementation ("de.hdodenhof:circleimageview:3.1.0")
        implementation ("com.squareup.picasso:picasso:2.71828")
        implementation ("androidx.activity:activity-ktx")
        implementation("com.github.denzcosKun:ImageSlideShow:0.1.2")


        val nav_version = "2.5.2" // Update with latest version if needed

        implementation("androidx.navigation:navigation-fragment:$nav_version")
        implementation ("androidx.navigation:navigation-ui:$nav_version")
        implementation ("com.facebook.shimmer:shimmer:0.5.0")


        implementation ("io.github.chaosleung:pinview:1.4.4")
        implementation("com.intuit.sdp:sdp-android:1.1.1")
        implementation("com.intuit.ssp:ssp-android:1.1.1")

        val lifecycle_version = "2.8.0"
        // ViewModel
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

        // LiveData
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

        //Room

        val room_version = "2.6.1"

        implementation("androidx.room:room-runtime:$room_version")
        annotationProcessor("androidx.room:room-compiler:$room_version")
        // To use Kotlin annotation processing tool (kapt)

        kapt("androidx.room:room-compiler:$room_version")
        implementation("androidx.room:room-ktx:$room_version")

        implementation("com.squareup.retrofit2:converter-gson:2.11.0")
        implementation ("com.squareup.retrofit2:retrofit:2.11.0")




    }


