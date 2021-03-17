plugins {
    java
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.dfsek"
version = "0.5.1"

repositories {
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.dfsek:Tectonic:1.2.3")
    implementation("com.dfsek.terra.common:common:4.3.0-BETA+6d51da31")
    implementation("org.yaml:snakeyaml:1.27")
    implementation("com.dfsek:Paralithic:0.3.2")
    implementation("commons-io:commons-io:2.8.0")
    implementation("net.jafama:jafama:2.3.2")
    implementation("org.ow2.asm:asm:9.0")

    implementation("com.fifesoft:rstaui:3.1.1")
    implementation("com.fifesoft:rsyntaxtextarea:3.1.2")


    implementation("com.formdev:flatlaf:1.0")
}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.dfsek.noise.NoiseTool"
    }
}