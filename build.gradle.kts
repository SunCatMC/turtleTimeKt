import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
}

val projectGroup = "me.suncatmc"

group = projectGroup
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register("generateMyResources") {
    outputs.dir("generatedResources")
    doLast {
        val generated = File("generatedResources", "project.properties")
        generated.writeText("version=${version}")
    }
}

sourceSets.main {
    java.srcDirs("src/main/kotlin/")
    output.dir(mapOf("builtBy" to tasks.named("generateMyResources")),"generatedResources")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "$projectGroup.${project.name}.MainKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from ({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}