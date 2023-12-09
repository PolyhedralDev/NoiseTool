plugins {
    java
    id("com.github.johnrengelman.shadow").version("8.1.1")
}

group = "com.dfsek"
version = "1.0.1"

repositories {
    mavenCentral()
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.10.1")
    implementation("com.dfsek.terra:base:6.4.1-BETA+3aef97738")
    compileOnly("org.jetbrains:annotations:24.1.0")
    implementation("commons-io:commons-io:2.15.1")
    implementation("com.google.guava:guava:32.1.3-jre")

    implementation("com.dfsek.tectonic", "yaml", "4.2.1")
    implementation("ch.qos.logback:logback-classic:1.4.14")


    implementation("com.fifesoft:rstaui:3.3.1")
    implementation("com.fifesoft:rsyntaxtextarea:3.3.4")
    implementation("com.fifesoft:autocomplete:3.3.1")

    implementation("com.formdev:flatlaf:3.2.5")
}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.dfsek.noise.NoiseTool"
    }
}

tasks.build {
    dependsOn(tasks.named("shadowJar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}