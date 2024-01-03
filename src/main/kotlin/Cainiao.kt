package dev.rlqd.alinotify

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.util.Properties

class Cainiao(appProperties: Properties) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val trackingNumber: String by appProperties

    suspend fun status(): CainiaoResponse {
        val response = client.get("https://global.cainiao.com/global/detail.json") {
            url {
                parameters.append("mailNos", trackingNumber)
                parameters.append("lang", "zh")
                parameters.append("language", "zh")
            }
        }
        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to fetch tracking status. API Response ${response.status}: " + response.bodyAsText())
        }
        return response.body()
    }
}
