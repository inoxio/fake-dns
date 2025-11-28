plugins {
    id("com.adarshr.test-logger") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    id("java")
    id("org.springframework.boot") version "4.0.0"
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
    implementation("org.apache.mina:mina-core:2.2.5")
    implementation("dnsjava:dnsjava:3.6.3")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_21
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
}
