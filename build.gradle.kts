plugins {
	id("java")
	id("com.gradleup.shadow") version "9.5.1"
	id("net.kyori.indra.checkstyle") version "4.0.0"
	id("com.github.ben-manes.versions") version "0.54.0"
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
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
	compileOnly("net.luckperms:api:5.5")
	implementation("love.broccolai.corn:corn-minecraft:4.1.0-SNAPSHOT")
	implementation("com.google.inject:guice:7.0.0")
	implementation("dev.tehbrian:agna-paper:1.0.6")
	implementation("org.incendo:cloud-paper:2.0.0-beta.17")
	implementation("org.spongepowered:configurate-hocon:4.2.0")
}

tasks {
	assemble {
		dependsOn(shadowJar)
	}

	processResources {
		filesMatching("plugin.yml") {
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
				"io.leangen",
				"jakarta.inject",
				"javax.annotation",
				"love.broccolai.corn",
				"net.kyori.option",
				"org.aopalliance",
				"org.checkerframework",
				"org.incendo.cloud",
				"org.spongepowered",
		)
	}
}
