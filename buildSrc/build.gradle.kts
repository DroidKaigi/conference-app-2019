buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.google.apis:google-api-services-androidpublisher:v3-rev46-1.25.0")
        classpath("com.google.api-client:google-api-client:1.28.0")
    }
}

plugins {
    `kotlin-dsl`
}
repositories {
    jcenter()
}

dependencies {
    implementation("com.google.guava:guava:26.0-jre")
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev46-1.25.0") {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation("com.google.api-client:google-api-client:1.28.0") {
        exclude(group = "com.google.guava", module = "guava")
    }
}
