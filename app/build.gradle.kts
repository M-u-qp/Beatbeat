plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.plugin.serialization)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.muqp.beatbeat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.muqp.beatbeat"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Serialization
    implementation(libs.kotlinx.serialization.json)

    //Dagger 2
    implementation(libs.dagger.android)
    implementation(libs.dagger.android.support)
    ksp(libs.dagger.android.processor)
    ksp(libs.dagger.compiler)

    //Splashscreen
    implementation(libs.androidx.core.splashscreen)

    //Datastore
    implementation(libs.androidx.datastore.preferences)

    //Retrofit
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //ExoPlayer
    implementation(libs.androidx.media3.exoplayer)

    //Logger
    implementation(libs.logging.interceptor)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.search)
    implementation(projects.feature.favorites)
    implementation(projects.feature.listen)
    implementation(projects.feature.details)
    implementation(projects.core.utils)
    implementation(projects.core.ui)
    implementation(projects.core.localData)
    implementation(projects.core.network)
    implementation(projects.exoPlayer)
}