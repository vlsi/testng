plugins {
    `maven-publish`
    signing
}

// Developers do not always have PGP configured,
// so activate signing for release versions only
// Just in case Maven Central rejects signed snapshots for some reason
if (!version.toString().endsWith("-SNAPSHOT") && System.getenv("JITPACK")?.toBoolean() != true) {
    signing {
        sign(publishing.publications)
    }
}
