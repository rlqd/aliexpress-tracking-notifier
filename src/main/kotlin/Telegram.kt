package dev.rlqd.alinotify

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.Properties

class Telegram(appProperties: Properties) {
    private val client = HttpClient(CIO)

    private val tgBotToken: String by appProperties
    private val tgChatId: String by appProperties

    suspend fun notify(text: String) {
        val response = client.get("https://api.telegram.org/bot${tgBotToken}/sendMessage") {
            url {
                parameters.append("chat_id", tgChatId)
                parameters.append("text", text)
            }
        }
        if (!response.status.isSuccess()) {
            throw RuntimeException("Failed to send TG Notification. API Response ${response.status}: " + response.bodyAsText())
        }
    }
}