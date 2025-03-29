plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    compileOnly("com.android.tools.lint:lint-api:31.9.1")
    compileOnly("com.android.tools.lint:lint-checks:31.9.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}