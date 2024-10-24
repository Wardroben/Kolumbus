plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.work"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(project(":core:data"))
}