plugins {
    id("kolumbus.android.feature")
    id("kolumbus.android.library.compose")
}

android {
    namespace = "ru.smalljinn.search"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
}