plugins {
    java
    application
    id("org.beryx.jlink") version "3.0.1"
    id("de.undercouch.download") version "5.6.0"
    kotlin("jvm")
}

group = "io.gitlab.jfronny"
version = "1.0"

application {
    mainClass.set("io.gitlab.jfronny.javagi.example.Main")
    mainModule.set("io.gitlab.jfronny.javagi.example")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.jwharm.javagi:adw:0.12.0") // or gtk for pure GTK
    implementation(kotlin("stdlib-jdk8"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

val os = org.gradle.internal.os.OperatingSystem.current()!!
val appName = "JavaGI Multiplatform Example"

if (os.isWindows) {
    val downloadNatives by tasks.registering(de.undercouch.gradle.tasks.download.Download::class) {
        src("https://github.com/jwharm/java-gi/releases/download/libraries/natives-adw.zip") // or natives-gtk for pure GTK
        dest(layout.buildDirectory.file("natives.zip"))
        overwrite(false)
    }

    tasks.jpackageImage {
        dependsOn(downloadNatives)
        doLast {
            copy {
                from(zipTree(downloadNatives.get().dest))
                into(layout.buildDirectory.dir("jpackage/$appName/app"))
            }
            copy {
                from(zipTree(downloadNatives.get().dest))
                into(layout.buildDirectory.dir("jpackage/$appName/runtime/bin"))
            }
        }
    }
}

jlink {
    addOptions(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--verbose"
    )
    launcher {
        name = appName
    }
    jpackage {
        vendor = "Some Corp"
        jvmArgs.addAll(
            listOf(
                "--enable-native-access=org.gnome.gtk",
                "--enable-native-access=org.gnome.glib",
                "--enable-native-access=org.gnome.gobject",
                "--enable-native-access=org.gnome.gio",
                "--enable-native-access=org.gnome.adw"
            )
        )
        if(os.isWindows){
            installerType = "msi" // Only generate the msi installer, since the behavior for MSIs and EXEs differs
            installerOptions.addAll(listOf(
                "--win-per-user-install",
                "--win-dir-chooser",
                "--win-menu",
                "--win-upgrade-uuid", "1d2e433e-f2e1-43bc-9cd4-60d1ec6b7833" // Update this UUID if you fork the project!!!
            ))
        }
        else {
            installerType = "rpm"
        }
    }
}