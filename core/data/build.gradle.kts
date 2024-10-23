plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:photo-store"))
    implementation(project(":core:datastore"))

    androidTestImplementation(libs.kotlinx.coroutines.test)
}