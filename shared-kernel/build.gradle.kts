plugins {
    `java-library`
}

// Dominio puro compartido entre bounded contexts: sin Spring, sin Mongo, sin dependencias de
// infraestructura. Si algo aqui necesita una dependencia externa, probablemente no pertenece
// al shared-kernel.
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
