plugins {
    java
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.dfsek"
version = "0.4.0"

repositories {
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.dfsek:Tectonic:1.2.3")
    implementation("com.dfsek.terra.common:common:4.0.1-BETA+f8cf61e2")
    implementation("org.yaml:snakeyaml:1.27")
    implementation("commons-io:commons-io:2.8.0")
    implementation("net.jafama:jafama:2.3.2")
}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.dfsek.noise.NoiseTool"
    }
}