plugins {
    id("java")
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "net.serlith.version.server"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val nettyVersion = "4.2.15.Final"
dependencies {
    // Spring tools
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Misc
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    implementation("io.jsonwebtoken:jjwt-impl:0.13.0")
    implementation("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // Database
    runtimeOnly("io.r2dbc:r2dbc-pool")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // Network
    runtimeOnly("io.netty:netty-transport-native-io_uring:$nettyVersion:linux-x86_64")
    runtimeOnly("io.netty:netty-transport-native-io_uring:$nettyVersion:linux-aarch_64")
    runtimeOnly("io.netty:netty-transport-native-kqueue:$nettyVersion:osx-x86_64")
    runtimeOnly("io.netty:netty-transport-native-kqueue:$nettyVersion:osx-aarch_64")
}

tasks.register("prepareKotlinBuildScriptModel") {
}
