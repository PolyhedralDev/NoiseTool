plugins {
    java
}

group = "com.dfsek"
version = "0.1.0"

repositories {
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation("com.dfsek:Tectonic:1.1.0")
    implementation("com.dfsek.terra.common:common:3.0.0-BETA+462b6f4")
    implementation("org.yaml:snakeyaml:1.27")
}
