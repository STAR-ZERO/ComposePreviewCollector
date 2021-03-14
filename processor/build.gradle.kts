plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

val kspVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
