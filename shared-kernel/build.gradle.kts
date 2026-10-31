plugins {
    `java-library`
}

// Dominio puro compartido entre bounded contexts: sin Spring, sin Mongo, sin dependencias de
// infraestructura. Si algo aqui necesita una dependencia externa, probablemente no pertenece
// al shared-kernel.
dependencies {
    // Solo para anotar @NullMarked: ver docs/adr/0001-harness-de-calidad.md.
    // No es una dependencia de infraestructura, es la unica excepcion
    // deliberada a la regla de admision de este modulo.
    compileOnly("org.jspecify:jspecify:1.0.0")
    testCompileOnly("org.jspecify:jspecify:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
