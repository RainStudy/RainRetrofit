import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`
    kotlin("jvm") version "1.5.10"
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "kim.bifrost.rain.retrofit"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://gitee.com/coldrain-moro/maven/raw/master/repo") }
}

dependencies {
    implementation("ink.ptms.tiphareth:tiphareth:1.4.0")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.withType<ShadowJar> {
    dependencies {
        include(dependency("com.google.code.gson:gson:2.8.9"))
        include(dependency("com.squareup.okhttp3:okhttp:4.9.3"))
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("maven") {
            artifactId = "retrofit"
            groupId = "kim.bifrost.rain"
            version = project.version.toString()
            println("> version $version")
            file("$buildDir/libs").listFiles()?.forEach { file ->
                if (file.extension == "jar") {
                    artifact(file) {
                        classifier = file.nameWithoutExtension.substring(0, file.nameWithoutExtension.length - project.version.toString().length - 1)
                        println("> module $classifier (${file.name})")
                    }
                }
            }
        }
    }
}