plugins {
    id("kolumbus.android.feature")
    id("kolumbus.android.library.compose")
}

android {
    namespace = "ru.smalljinn.places"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}