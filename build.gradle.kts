plugins {
    id("java")
    id("application")
}

group = "com.pandaer"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("com.pandaer.code.review.agent.cli.Main")
}

repositories {
    mavenCentral()
}

val versions = mapOf(
    "picocli" to "4.7.7",
    "okhttp" to "4.12.0",
    "jackson" to "2.21.4",
    "slf4j" to "2.0.18",
    "logback" to "1.5.34",
    "junit" to "5.14.4"
)

dependencies {
    // CLI
    implementation("info.picocli:picocli:${versions["picocli"]}")

    // HTTP Client
    implementation("com.squareup.okhttp3:okhttp:${versions["okhttp"]}")
    implementation("com.squareup.okhttp3:okhttp-sse:${versions["okhttp"]}")

    // JSON + YAML
    implementation("com.fasterxml.jackson.core:jackson-core:${versions["jackson"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${versions["jackson"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${versions["jackson"]}")

    // Logging
    implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")
    runtimeOnly("ch.qos.logback:logback-classic:${versions["logback"]}")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:${versions["junit"]}")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}
