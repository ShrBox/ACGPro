import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

plugins {
    kotlin("jvm") version "1.3.71"
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "shrbox.github.acg"
version = "0.1.0"

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://mirrors.huaweicloud.com/repository/maven")
    mavenCentral()
    jcenter()
}

val miraiCoreVersion = "1.2.2"
val miraiConsoleVersion = "0.5.2"

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("net.mamoe:mirai-core:$miraiCoreVersion")
    compileOnly("net.mamoe:mirai-console:$miraiConsoleVersion")

    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation("net.mamoe:mirai-core:$miraiCoreVersion")
    testImplementation("net.mamoe:mirai-core-qqandroid:$miraiCoreVersion")
    testImplementation("net.mamoe:mirai-console:$miraiConsoleVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    val runMiraiConsole by creating(JavaExec::class.java) {
        group = "mirai"
        dependsOn(shadowJar)
        dependsOn(testClasses)

        val testConsoleDir = "test"

        doFirst {
            fun removeOldVersions() {
                File("$testConsoleDir/plugins/").walk()
                    .filter { it.name.matches(Regex("""${project.name}-.*-all.jar""")) }
                    .forEach {
                        it.delete()
                        println("deleting old files: ${it.name}")
                    }
            }

            fun copyBuildOutput() {
                File("build/libs/").walk()
                    .filter { it.name.contains("-all") }
                    .maxBy { it.lastModified() }
                    ?.let {
                        println("Coping ${it.name}")
                        it.inputStream()
                            .transferTo1(File("$testConsoleDir/plugins/${it.name}").apply { createNewFile() }
                                .outputStream())
                        println("Copied ${it.name}")
                    }
            }

            workingDir = File(testConsoleDir)
            workingDir.mkdir()
            File(workingDir, "plugins").mkdir()
            removeOldVersions()
            copyBuildOutput()

            classpath = sourceSets["test"].runtimeClasspath
            main = "mirai.RunMirai"
            standardInput = System.`in`
            args(miraiCoreVersion, miraiConsoleVersion)
        }
    }
}

@Throws(IOException::class)
fun InputStream.transferTo1(out: OutputStream): Long {
    Objects.requireNonNull(out, "out")
    var transferred: Long = 0
    val buffer = ByteArray(8192)
    var read: Int
    while (this.read(buffer, 0, 8192).also { read = it } >= 0) {
        out.write(buffer, 0, read)
        transferred += read.toLong()
    }
    return transferred
}