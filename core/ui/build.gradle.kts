plugins {
    id("kolumbus.android.library")
    id("kolumbus.android.library.compose")
}

android {
    namespace = "ru.smalljinn.ui"
}

dependencies {
    api(project(":core:model"))
    api(libs.androidx.material3)
    //api(libs.androidx.compose.ui)
    implementation(libs.coil)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}