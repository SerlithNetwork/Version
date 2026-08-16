plugins {
    java
    id("io.freefair.lombok") version "9.4.0"
}

group = "net.serlith.version"
version = "0.0.1-SNAPSHOT"
description = "Version"

allprojects {
    apply {
        plugin("java")
        plugin("io.freefair.lombok")
    }

    repositories {
        mavenCentral()
        maven("https://jitpack.io/")
    }
}

dependencies {
}

subprojects {
}

