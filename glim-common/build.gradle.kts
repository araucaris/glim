plugins {
    `glim-java`
    `glim-publish`
    `glim-repositories`
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    val adventure = "4.17.0"
    api("net.kyori:adventure-text-minimessage:$adventure")
    api("net.kyori:adventure-text-serializer-legacy:$adventure")
}

glimPublish {
    artifactId = "glim-common"
}