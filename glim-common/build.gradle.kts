plugins {
    `glim-java`
    `glim-publish`
    `glim-repositories`
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnlyApi("com.mojang:authlib:1.5.25")

    val adventure = "4.17.0"
    api("net.kyori:adventure-text-minimessage:$adventure")
    api("net.kyori:adventure-text-serializer-legacy:$adventure")
}

glimPublish {
    artifactId = "glim-common"
}
