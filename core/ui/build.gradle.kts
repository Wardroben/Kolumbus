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
    implementation(project(":core:permissions"))

    //implementation(libs.gms.maps)
    implementation(libs.gms.location)
    implementation(libs.maps.compose)

    //api(libs.androidx.compose.ui)
    implementation(libs.coil)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}