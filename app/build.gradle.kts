plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "id.dpradana.themoviedb"
    compileSdk = 37

    defaultConfig {
        applicationId = "id.dpradana.themoviedb"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":feature:genre"))
    implementation(project(":feature:movie"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:reviews"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
}