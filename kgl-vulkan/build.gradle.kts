import org.gradle.internal.os.OperatingSystem

plugins {
	kotlin("multiplatform")
}

kotlin {
	val os = OperatingSystem.current()
	val isIdeaActive = System.getProperty("idea.active") == "true"

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(kotlin("stdlib-common"))
				api(project(":kgl-core"))
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test-common"))
				implementation(kotlin("test-annotations-common"))
			}
		}
	}

	jvm {
		compilations["main"].defaultSourceSet {
			dependencies {
				implementation(kotlin("stdlib-jdk8"))
				api("org.lwjgl:lwjgl-vulkan:${extra["lwjglVersion"]}")
			}
		}
		compilations["test"].defaultSourceSet {
			dependencies {
				implementation(kotlin("test"))
				implementation(kotlin("test-junit"))
				implementation("org.lwjgl:lwjgl:${extra["lwjglVersion"]}:${extra["lwjglNatives"]}")
			}
		}
	}
	
	val vulkanHeaderDir = project.file("src/nativeInterop/vulkan/include")

	if (os.isWindows || !isIdeaActive) {
		mingwX64("mingw") {
			compilations["main"].cinterops.apply {
				create("cvulkan") {
					includeDirs(vulkanHeaderDir)
				}
			}
			compilations["main"].defaultSourceSet {
				kotlin.srcDir("src/nativeMain/kotlin")
				kotlin.srcDir("src/mingwMain/kotlin")
				resources.srcDir("src/nativeMain/resources")
			}
			compilations["test"].defaultSourceSet {
				kotlin.srcDir("src/nativeTest/kotlin")
				resources.srcDir("src/nativeTest/resources")
			}
		}
	}
	if (os.isLinux || !isIdeaActive) {
		linuxX64("linux") {
			compilations["main"].cinterops.apply {
				create("cvulkan") {
					includeDirs(vulkanHeaderDir)
				}
			}
			compilations["main"].defaultSourceSet {
				kotlin.srcDir("src/nativeMain/kotlin")
				kotlin.srcDir("src/linuxMain/kotlin")
				resources.srcDir("src/nativeMain/resources")
			}
			compilations["test"].defaultSourceSet {
				kotlin.srcDir("src/nativeTest/kotlin")
				resources.srcDir("src/nativeTest/resources")
			}
		}
	}
	if (os.isMacOsX || !isIdeaActive) {
		macosX64("macos") {
			compilations["main"].cinterops.apply {
				create("cvulkan") {
					includeDirs(vulkanHeaderDir)
				}
			}
			compilations["main"].defaultSourceSet {
				kotlin.srcDir("src/nativeMain/kotlin")
				kotlin.srcDir("src/macosMain/kotlin")
				resources.srcDir("src/nativeMain/resources")
			}
			compilations["test"].defaultSourceSet {
				kotlin.srcDir("src/nativeTest/kotlin")
				resources.srcDir("src/nativeTest/resources")
			}
		}
	}
}

apply {
	from(rootProject.file("gradle/publish.gradle"))
}
