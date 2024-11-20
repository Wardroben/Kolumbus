plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ru.smalljinn.import_export"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(libs.kotlinx.serialization.cbor)
}