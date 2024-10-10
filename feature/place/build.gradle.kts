plugins {
    id("kolumbus.android.feature")
    id("kolumbus.android.library.compose")
}

android {
    namespace = "ru.smalljinn.place"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:permissions"))
}