plugins {
    id("com.adarshr.test-logger") version "3.2.0"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("jacoco")
    id("java")
    id("org.springframework.boot") version "2.7.0"
}

group = "de.inoxio"
version = file("version.txt").readText().trim()

configurations {
    compileOnly {
        extendsFrom(configurations["annotationProcessor"])
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.apache.mina:mina-core:2.2.1")
    implementation("dnsjava:dnsjava:3.5.1")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    compileJava {
        options.apply {
            isFork = true
            isIncremental = true
            encoding = "UTF-8"
            compilerArgs = mutableListOf("-Xlint", "-Xlint:-processing")
        }
    }
    test {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }
    jacocoTestReport {
        reports { xml.apply { required.set(true) } }
        dependsOn("test")
    }
    dependencyUpdates {
        rejectVersionIf {
            listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "pr").any { qualifier ->
                "(?i).*[.-]$qualifier[.\\d-+]*".toRegex().matches(candidate.version)
            }
        }
    }
    springBoot {
        bootBuildImage {
            imageName = "ghcr.io/inoxio/fake-dns:${project.version}"
            docker {
                publishRegistry {
                    username = System.getenv("CI_REGISTRY_USER")
                    password = System.getenv("CI_REGISTRY_PASSWORD")
                }
            }
        }
    }
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "7.3.3"
    }
}
