plugins {
    `maven-publish`
    `signing`
}

signing {
    sign(publishing.publications)
}
