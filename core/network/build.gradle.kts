plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "id.dpradana.themoviedb.core.network"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    buildFeatures {
        buildConfig = true
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
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    api(libs.okhttp)
    api(libs.okhttp.logging.interceptor)

    api(libs.dagger)
    ksp(libs.dagger.compiler)
}
