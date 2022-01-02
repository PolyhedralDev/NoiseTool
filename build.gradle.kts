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
    implementation("com.dfsek.terra:base:6.0.0-BETA+f7d53c9db")
    compileOnly("org.jetbrains:annotations:23.0.0")


    implementation("com.fifesoft:rstaui:3.1.3")
    implementation("com.fifesoft:rsyntaxtextarea:3.1.4")
    implementation("com.fifesoft:autocomplete:3.1.3")

    implementation("com.formdev:flatlaf:1.6.5")
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