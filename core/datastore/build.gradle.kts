plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.datastore"
}

dependencies {
    api(project(":core:model"))
    api(project(":core:di"))

    implementation(libs.androidx.datastore.preferences)
}