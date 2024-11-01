plugins {
    `glim-java`
    `glim-publish`
    `glim-repositories`
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

dependencies {
    compileOnlyApi("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnlyApi("com.mojang:authlib:1.5.25")

    val adventure = "4.17.0"
    api("net.kyori:adventure-text-minimessage:$adventure")
    api("net.kyori:adventure-text-serializer-legacy:$adventure")
}

glimPublish {
    artifactId = "glim-common"
}

tasks {
    runServer {
        minecraftVersion("1.21.1")

        jvmArgs(
            "-Dcom.mojang.eula.agree=true"
        )
    }
}
