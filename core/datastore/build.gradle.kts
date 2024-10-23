plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.datastore"
}

dependencies {
    api(project(":core:model"))

    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.kotlinx.coroutines.test)
}