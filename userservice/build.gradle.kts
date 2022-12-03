import com.kobylynskyi.graphql.codegen.model.ApiNamePrefixStrategy
import com.kobylynskyi.graphql.codegen.model.ApiRootInterfaceStrategy
import io.github.kobylynskyi.graphql.codegen.gradle.GraphQLCodegenGradleTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.7.21"

    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.18"
    id("io.github.kobylynskyi.graphql.codegen") version "5.5.0"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion // Needed for mapstruct, see https://github.com/mapstruct/mapstruct/issues/2522
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
}

allOpen {
    annotations("jakarta.persistence.Entity", "jakarta.persistence.MappedSuperclass", "jakarta.persistence.Embedabble")
}

group = "com.example.graphql"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    implementation("org.liquibase:liquibase-core")

    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")

    //runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.graphql:spring-graphql-test")
}

val graphqlOutputDir = "$buildDir/generated/sources/graphql/"

tasks {
    withType<KotlinCompile> {
        dependsOn("graphqlCodegen")
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        archiveVersion.set("")
        archiveBaseName.set("service")
    }
    named<GraphQLCodegenGradleTask>("graphqlCodegen") {
        // all config options: https://github.com/kobylynskyi/graphql-java-codegen/blob/master/docs/codegen-options.md
        graphqlSchemas.rootDir = "$projectDir/src/main/resources/graphql/"
        addGeneratedAnnotation = false
        outputDir = File(graphqlOutputDir)
        packageName = "com.example.graphql.generated"
        apiPackageName = "com.example.graphql.generated"
        apiRootInterfaceStrategy = ApiRootInterfaceStrategy.INTERFACE_PER_SCHEMA
        apiNamePrefixStrategy = ApiNamePrefixStrategy.FILE_NAME_AS_PREFIX
        apiInterfaceStrategy = com.kobylynskyi.graphql.codegen.model.ApiInterfaceStrategy.DO_NOT_GENERATE
        apiNameSuffix = "Api"
        modelNameSuffix = "DTO"
        generateDataFetchingEnvironmentArgumentInApis = true
        customTypesMapping = mutableMapOf(Pair("ID", "java.lang.Long"))
        modelValidationAnnotation = "@jakarta.validation.constraints.NotNull"
    }
}

kapt {
    arguments {
        arg("mapstruct.unmappedTargetPolicy", "ignore")
    }
}

java {
    sourceSets {
        main {
            java.srcDirs(graphqlOutputDir)
        }
    }
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDirs(graphqlOutputDir)
        }
    }
}
