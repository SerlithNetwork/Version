plugins {
    id("java")
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jooq.jooq-codegen-gradle") version "3.21.5"
    id("org.flywaydb.flyway") version "12.8.1"
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
val jooqVersion = "3.21.5"

dependencies {
    // Spring tools
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Jooq
    jooqCodegen("org.jooq:jooq-codegen:$jooqVersion")
    jooqCodegen("com.h2database:h2")

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

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:12.8.1")
    }
}

val script = projectDir.resolve("src/main/resources/db/migration/V1__init_schema.sql")

flyway {
    url = property("version.jooq.database.url") as String
    user = property("version.jooq.database.user") as String
    password = property("version.jooq.database.password") as String
    locations = arrayOf("filesystem:${script.parent}")
    driver = "org.postgresql.Driver"
    cleanDisabled = false

    dependencies {
        runtimeOnly("org.postgresql:postgresql")
    }
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = property("version.jooq.database.url") as String
            user = property("version.jooq.database.user") as String
            password = property("version.jooq.database.password") as String
        }
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
            }
            generate {
                isRecords = true
            }
            target {
                packageName = "io.canvasmc.gordonramsay.schema"
                directory = "build/generated-src/jooq/main"
            }
        }
    }
}

tasks {
    compileJava {
        dependsOn(jooqCodegen)
    }
    jooqCodegen {
        dependsOn(flywayMigrate)
    }
    flywayMigrate {
        dependsOn(flywayClean)
    }
}
