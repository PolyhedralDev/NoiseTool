plugins {
    java
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.dfsek"
version = "1.0.0"

repositories {
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.dfsek:Tectonic:1.2.3")
    implementation("com.dfsek.terra.common:common:5.1.3-BETA+f396e0e5")
    implementation("org.yaml:snakeyaml:1.27")
    implementation("com.dfsek:Paralithic:0.3.2")
    implementation("commons-io:commons-io:2.8.0")
    implementation("net.jafama:jafama:2.3.2")
    implementation("org.ow2.asm:asm:9.0")

    implementation("com.fifesoft:rstaui:3.1.1")
    implementation("com.fifesoft:rsyntaxtextarea:3.1.2")
    implementation("com.fifesoft:autocomplete:3.1.1")


    implementation("com.formdev:flatlaf:1.0")
}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.dfsek.noise.NoiseTool"
    }
}