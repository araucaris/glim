plugins {
    `glim-java`
    `glim-repositories`
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.github.goooler.shadow") version "8.1.8"
}

dependencies {
    implementation(project(":glim-common"))
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.JETBRAINS
    }
}

tasks {
    runServer {
        jvmArgs("-XX:+AllowEnhancedClassRedefinition")

        minecraftVersion("1.21.1")

        jvmArgs(
            "-Dcom.mojang.eula.agree=true"
        )
    }
}
