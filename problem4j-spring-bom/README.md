# BOM of Problem4J

[![Sonatype](https://img.shields.io/maven-central/v/io.github.malczuuu.problem4j/problem4j-spring-bom?filter=1.0.*)](https://central.sonatype.com/artifact/io.github.malczuuu.problem4j/problem4j-spring-bom)

Bill Of Materials (BOM) for the Spring integrations of **Problem4J**, a library implementing *RFC 7807 - Problem Details
for HTTP APIs*.

Importing this BOM lets you declare the individual `problem4j-*` Spring modules **without repeating versions** and keeps
all components aligned.

## Using the BOM

### Gradle (Kotlin DSL)

Add the BOM to `implementation(platform(...))`, then declare modules without versions.

```kotlin
dependencies {
    implementation(platform("io.github.malczuuu.problem4j:problem4j-spring-bom:{version}"))

    implementation("io.github.malczuuu.problem4j:problem4j-core")
    implementation("io.github.malczuuu.problem4j:problem4j-jackson")
    implementation("io.github.malczuuu.problem4j:problem4j-spring-web")
    implementation("io.github.malczuuu.problem4j:problem4j-spring-webmvc")
    implementation("io.github.malczuuu.problem4j:problem4j-spring-webflux")
}
```

### Maven

Add the BOM to `<dependencyManagement>` with `import` scope, then declare modules without versions.

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.malczuuu.problem4j</groupId>
            <artifactId>problem4j-spring-bom</artifactId>
            <version>{version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
<dependency>
    <groupId>io.github.malczuuu.problem4j</groupId>
    <artifactId>problem4j-core</artifactId>
</dependency>
<dependency>
    <groupId>io.github.malczuuu.problem4j</groupId>
    <artifactId>problem4j-jackson</artifactId>
</dependency>
<dependency>
    <groupId>io.github.malczuuu.problem4j</groupId>
    <artifactId>problem4j-spring-web</artifactId>
</dependency>
<dependency>
    <groupId>io.github.malczuuu.problem4j</groupId>
    <artifactId>problem4j-spring-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>io.github.malczuuu.problem4j</groupId>
    <artifactId>problem4j-spring-webflux</artifactId>
</dependency>
</dependencies>
```
