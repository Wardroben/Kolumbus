plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.domain"
}

dependencies {
    implementation(project(":core:model"))
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
}