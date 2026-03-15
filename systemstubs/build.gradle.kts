plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jlleitschuh.gradle.ktlint)
}

android {
    namespace = "io.github.sds100.keymapper.systemstubs"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        create("debug_release") {
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        aidl = true
    }
}

// Workaround for Windows-only AIDL bug: the AIDL compiler embeds the command it ran as a
// Javadoc comment in every generated .java file, e.g.:
//   * Using: C:\Users\...
// Java processes unicode escapes (e.g. \U) even inside comments before parsing, so \U in
// a Windows path causes "illegal unicode escape" errors in javac.
// This task runs after each AIDL compile task and replaces backslashes in those comment
// lines with forward slashes so javac is happy.
androidComponents {
    onVariants { variant ->
        val variantName = variant.name.replaceFirstChar { it.uppercaseChar() }
        val fixTaskName = "fixAidlUnicodeEscapes$variantName"

        val fixTask = tasks.register(fixTaskName) {
            val aidlOutputDir = layout.buildDirectory.dir(
                "generated/aidl_source_output_dir/${variant.name}/out",
            )
            inputs.dir(aidlOutputDir)
            outputs.dir(aidlOutputDir)
            doLast {
                aidlOutputDir.get().asFile.walkTopDown()
                    .filter { it.isFile && it.extension == "java" }
                    .forEach { file ->
                        val original = file.readText()
                        // Only touch lines that are part of the "Using:" command comment to
                        // avoid accidentally altering any real source content.
                        val patched = original.lines().joinToString("\n") { line ->
                            if (line.trimStart().startsWith("* Using:")) {
                                line.replace('\\', '/')
                            } else {
                                line
                            }
                        }
                        if (patched != original) {
                            file.writeText(patched)
                        }
                    }
            }
        }

        // Run after AIDL compilation, before any compile task that reads the generated sources
        tasks.matching { it.name == "compile${variantName}Aidl" }.configureEach {
            finalizedBy(fixTask)
        }
        tasks.matching {
            it.name == "compile${variantName}JavaWithJavac" ||
                it.name == "compile${variantName}Kotlin"
        }.configureEach {
            dependsOn(fixTask)
        }
    }
}

dependencies {
    implementation(libs.androidx.annotation.jvm)
}
