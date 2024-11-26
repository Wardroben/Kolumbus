plugins {
    id("kolumbus.android.library")
    id("kolumbus.hilt")
}

android {
    namespace = "ru.smalljinn.work"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:di"))
    ksp(libs.hilt.ext.compiler)

    implementation(project(":core:data"))
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
}