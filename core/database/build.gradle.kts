plugins {
    id("kolumbus.android.library")
    id("kolumbus.android.room")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.database"
}

dependencies {
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}