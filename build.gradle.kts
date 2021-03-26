/*
 * Copyright 2016-2020 Open Food Facts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion by extra("1.4.21")
    val jacksonVersion by extra("2.12.0")
    val greendaoVersion by extra("3.3.0")
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath("org.greenrobot:greendao-gradle-plugin:$greendaoVersion")
        classpath("com.github.timfreiheit:ResourcePlaceholdersPlugin:0.2")

        classpath("io.sentry:sentry-android-gradle-plugin:1.7.36")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        classpath("org.jacoco:org.jacoco.core:0.8.6")
        classpath("com.vanniktech:gradle-android-junit-jacoco-plugin:0.16.0")
    }
}

plugins {
    id("org.sonarqube") version "3.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
    }

    sonarqube {
        properties {
            property("sonar.exclusions", "**/openfoodfacts/github/scrachx/openfood/models/*,**/*.xml")
            property("sonar.coverage.exclusions", "**/openfoodfacts/github/scrachx/openfood/models/*")
        }
    }
    
}
