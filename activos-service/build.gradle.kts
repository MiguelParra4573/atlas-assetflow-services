plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

dependencies {
    implementation(project(":shared-kernel"))

    // Anotaciones de nulabilidad para @NullMarked. Framework 6.1 (Boot 3.3.4)
    // usa org.springframework.lang.Nullable, no JSpecify todavia: esta
    // dependencia es solo para anotar el dominio propio, no interactua con
    // las firmas de Spring. Ver docs/adr/0001-harness-de-calidad.md.
    compileOnly("org.jspecify:jspecify:1.0.0")
    testCompileOnly("org.jspecify:jspecify:1.0.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.20.1")
    testImplementation("org.testcontainers:mongodb:1.20.1")
}
