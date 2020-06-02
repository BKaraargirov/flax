
plugins {
    id("org.jetbrains.kotlin.jvm")
}

val kmongoVersion = "3.11.0"


dependencies {
    implementation(project(":core"))

    implementation("org.litote.kmongo:kmongo:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-shared:$kmongoVersion")
}