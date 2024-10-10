plugins {
    java
    id("com.gradleup.shadow").version("8.3.3")
}

group = "com.dfsek"
version = "1.1.1"

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
    maven {
        name = "JogAmp"
        url = uri("https://jogamp.org/deployment/maven/")
    }
}

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.11.2")
    implementation("com.dfsek.terra:base:6.5.0-BETA+c374c2d5e")
    compileOnly("org.jetbrains:annotations:26.0.0")
    implementation("commons-io:commons-io:2.17.0")
    implementation("com.google.guava:guava:33.3.1-jre")

    implementation("com.dfsek.tectonic", "yaml", "4.2.1")
    implementation("ch.qos.logback:logback-classic:1.5.8")


    implementation("com.fifesoft:rstaui:3.3.1")
    implementation("com.fifesoft:rsyntaxtextarea:3.5.1")
    implementation("com.fifesoft:autocomplete:3.3.1")

    implementation("com.formdev:flatlaf:3.5.1")

    implementation("org.jogamp.jogl:jogl-all:2.5.0")
    runtimeOnly("org.jogamp.jogl:jogl-all:2.5.0:natives-linux-amd64")
    runtimeOnly("org.jogamp.jogl:jogl-all:2.5.0:natives-macosx-universal")
    runtimeOnly("org.jogamp.jogl:jogl-all:2.5.0:natives-windows-amd64")

    implementation("org.jogamp.gluegen:gluegen-rt:2.5.0")
    runtimeOnly("org.jogamp.gluegen:gluegen-rt:2.5.0:natives-linux-amd64")
    runtimeOnly("org.jogamp.gluegen:gluegen-rt:2.5.0:natives-macosx-universal")
    runtimeOnly("org.jogamp.gluegen:gluegen-rt:2.5.0:natives-windows-amd64")
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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}