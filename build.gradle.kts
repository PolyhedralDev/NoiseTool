plugins {
    java
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.dfsek"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("com.dfsek.terra:base:6.0.0-BETA+42fb3642f")
    compileOnly("org.jetbrains:annotations:23.0.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.guava:guava:31.1-jre")

    implementation("com.dfsek.tectonic", "yaml", "4.2.0")
    implementation("ch.qos.logback:logback-classic:1.2.11")


    implementation("com.fifesoft:rstaui:3.2.0")
    implementation("com.fifesoft:rsyntaxtextarea:3.2.0")
    implementation("com.fifesoft:autocomplete:3.2.0")

    implementation("com.formdev:flatlaf:2.1")
}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.dfsek.noise.NoiseTool"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}