// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "2.0.0" apply false
}
