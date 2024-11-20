plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.domain"
}

dependencies {
    implementation(project(":core:model"))
}