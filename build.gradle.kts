plugins {
    java
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.dfsek"
version = "0.2.0"

repositories {
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.dfsek:Tectonic:1.1.0")
    implementation("com.dfsek.terra.common:common:3.1.0-BETA+1ee2b18")
    implementation("org.yaml:snakeyaml:1.27")
    implementation("commons-io:commons-io:2.8.0")
    implementation("net.jafama:jafama:2.3.2")
}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.dfsek.noise.NoiseTool"
    }
}