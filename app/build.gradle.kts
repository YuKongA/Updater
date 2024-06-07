@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.io.ByteArrayOutputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "top.yukonga.update"
    compileSdk = 34

    defaultConfig {
        applicationId = "top.yukonga.update"
        minSdk = 26
        targetSdk = 34
        versionCode = getVersionCode()
        versionName = "1.3" + "-" + getVersionName()
    }
    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    if (keystorePath != null) {
        signingConfigs {
            register("github") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    } else {
        signingConfigs {
            register("release") {
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs.getByName(if (keystorePath != null) "github" else "release")
        }
        debug {
            if (keystorePath != null) signingConfig = signingConfigs.getByName("github")
            applicationIdSuffix = ".debug"
        }
    }
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs = listOf(
                "-Xno-param-assertions",
                "-Xno-call-assertions",
                "-Xno-receiver-assertions",
                "-language-version=2.0"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    androidResources {
        generateLocaleConfig = true
    }
    packaging {
        resources {
            excludes += "**"
        }
        applicationVariants.all {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName =
                    "Updater-$versionName($versionCode)-$name.apk"
            }
        }
    }
}

fun getGitCommitCount(): Int {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-list", "--count", "HEAD")
        standardOutput = out
    }
    return out.toString().trim().toInt()
}

fun getGitDescribe(): String {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--always")
        standardOutput = out
    }
    return out.toString().trim()
}

fun getVersionCode(): Int {
    val commitCount = getGitCommitCount()
    val major = 5
    return major + commitCount
}

fun getVersionName(): String {
    return getGitDescribe()
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.material)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}