plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "ru.smalljinn.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:import_export"))
    implementation(project(":core:domain"))
    implementation(project(":core:di"))

    implementation(libs.kotlinx.serialization.cbor)

    implementation(libs.coil)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
}