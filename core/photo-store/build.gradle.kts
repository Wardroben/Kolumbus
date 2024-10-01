plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.photo_store"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.datetime)
    implementation(project(":core:model"))

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}