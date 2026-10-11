plugins {
	id("java")
	id("com.gradleup.shadow") version "9.6.1"
	id("net.kyori.indra.checkstyle") version "4.0.0"
	id("io.github.ben-manes.versions") version "0.65.0"
}

group = "city.skybound"
version = "0.1.0-SNAPSHOT"
description = "The core, monolithic plugin for Skybound City."

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
	maven("https://repo.tehbrian.dev/releases/")
	maven("https://repo.broccol.ai/snapshots/")
	maven("https://repo.wyck.dev/snapshots/")
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:26.3.build.+")
	compileOnly("net.luckperms:api:5.5")
	compileOnly("org.jspecify:jspecify:1.0.1")
	implementation("com.google.inject:guice:7.0.0")
	// replace old lib bundled by Guice
	implementation("com.google.guava:guava:33.6.0-jre")
	implementation("dev.tehbrian:agna-paper:1.2.4")
	implementation("dev.tehbrian:agna-configurate:1.2.4")
	implementation("org.incendo:cloud-paper:2.0.1")
	implementation("org.spongepowered:configurate-hocon:4.2.0")
	implementation("dev.wyck:wyck:4.0.0-837d6ec")
	implementation("dev.wyck:wyck-decoders:4.0.0-837d6ec")
}

tasks {
	assemble {
		dependsOn(shadowJar)
	}

	processResources {
		filesMatching("paper-plugin.yml") {
			expand(
					mapOf(
							"version" to project.version,
							"description" to project.description
					)
			)
		}
	}

	base {
		archivesName.set("Helios")
	}

	shadowJar {
		archiveClassifier.set("")

		val libsPackage = "${project.group}.${project.name}.libs"
		fun moveToLibs(vararg patterns: String) {
			for (pattern in patterns) {
				relocate(pattern, "$libsPackage.$pattern")
			}
		}

		moveToLibs(
				"com.google.common",
				"com.google.errorprone",
				"com.google.inject",
				"com.google.j2objc",
				"com.google.thirdparty",
				"dev.tehbrian.agna",
				"dev.wyck",
				"io.leangen",
				"jakarta.inject",
				"javax.annotation",
				"net.kyori.option",
				"org.aopalliance",
				"org.checkerframework",
				"org.incendo.cloud",
				"org.spongepowered",
		)
	}
}
