// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val composeVersion: String by extra("1.0.0-beta02")
    val kspVersion: String by extra("1.4.30-1.0.0-alpha05")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha09")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
