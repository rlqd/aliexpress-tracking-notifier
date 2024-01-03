package dev.rlqd.alinotify

import kotlinx.coroutines.*
import java.io.*
import java.util.*
import kotlin.concurrent.timer
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import kotlin.time.Duration.Companion.milliseconds

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val propertiesFile = File("app.properties")
    println("Loading configuration file from: ${propertiesFile.absolutePath}")
    if (!propertiesFile.isFile) {
        throw RuntimeException("Can't find file ${propertiesFile.name}."
            + " Please check current directory or create properties file (see app.properties.example).")
    }
    val properties = Properties()
    withContext(Dispatchers.IO) {
        FileInputStream(propertiesFile).use(properties::load)
    }

    val tracking = Cainiao(properties)
    val tg = Telegram(properties)
    val json = Json {
        prettyPrint = true
    }

    var previous: CainiaoResponse? = null
    val cacheFile = File("app.cache")
    println("Loading previous tracking status from: ${cacheFile.absolutePath}")
    if (cacheFile.isFile) {
        withContext(Dispatchers.IO) {
            FileReader(cacheFile).use {
                previous = json.decodeFromString(it.readText())
            }
        }
    } else {
        println("No cache file found, we will send an update and write cache file now")
    }

    val checkPeriod = properties.getProperty("checkPeriod").toLong()
    checkPeriod.milliseconds.toComponents { hours, minutes, seconds, _ ->
        val periodStr = StringBuilder()
        if (hours > 0) {
            periodStr.append("${hours}h")
        }
        if (minutes > 0) {
            periodStr.append("${minutes}m")
        }
        if (seconds > 0) {
            periodStr.append("${seconds}s")
        }
        println("Starting check every ${periodStr}, first check right now")
    }

    timer(startAt = Date(), period = checkPeriod) {
        runBlocking {
            try {
                println("Requesting status")
                val current = tracking.status()
                println("Request finished. $current")
                if (current != previous) {
                    println("Change detected, sending notification")
                    tg.notify(current.toString())
                    previous = current
                    println("Updating cache file: ${cacheFile.absolutePath}")
                    FileWriter(cacheFile, false).use {
                        it.write(json.encodeToString(current))
                    }
                } else {
                    println("No change detected")
                }
            } catch (e: Throwable) {
                println("Failed to finish current check: $e")
            }
        }
    }
}
