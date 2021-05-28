import buildlogic.filterEolSimple

plugins {
    `java-library`
    id("testng.java")
    id("testng.testing")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Javadoc>().configureEach {
    excludes.add("org/testng/internal/**")
}

tasks.withType<JavaCompile>().configureEach {
    inputs.property("java.version", System.getProperty("java.version"))
    inputs.property("java.vm.version", System.getProperty("java.vm.version"))
    options.apply {
        encoding = "UTF-8"
        compilerArgs.add("-Xlint:deprecation")
        compilerArgs.add("-Werror")
    }
}

tasks.withType<Jar>().configureEach {
    into("META-INF") {
        filterEolSimple("crlf")
        from("$rootDir/LICENSE")
        from("$rootDir/NOTICE")
    }
    manifest {
        val name = "TestNG"
        val vendor = name
        attributes(mapOf(
            "Specification-Title" to name,
            "Specification-Version" to project.version,
            "Specification-Vendor" to vendor,
            "Implementation-Title" to name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to vendor,
            "Implementation-Vendor-Id" to "org.testng",
            "Implementation-Url" to findProperty("project.url"),
        ))
    }
}
