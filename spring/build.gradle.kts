plugins {
    id("org.jetbrains.kotlin.jvm")
}

val springVersion = "5.2.8.RELEASE"

dependencies {
    implementation("org.springframework:spring-core:$springVersion")
    implementation("org.springframework:spring-context:$springVersion")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
}
