plugins {
    java
    id("com.gradleup.shadow").version("8.3.3")
}

group = "com.dfsek"
version = "1.2.1"

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
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.13.0")
    testRuntimeOnly("org.junit.platform", "junit-platform-launcher")
    implementation("com.dfsek.terra:base:6.6.2-BETA+af9fb211a")
    compileOnly("org.jetbrains:annotations:26.0.2")
    implementation("commons-io:commons-io:2.19.0")
    implementation("com.google.guava:guava:33.4.8-jre")

    implementation("com.dfsek.tectonic", "yaml", "4.2.1")
    implementation("ch.qos.logback:logback-classic:1.5.18")


    implementation("com.fifesoft:rstaui:3.3.1")
    implementation("com.fifesoft:rsyntaxtextarea:3.5.1")
    implementation("com.fifesoft:autocomplete:3.3.1")

    implementation("com.formdev:flatlaf:3.6")

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