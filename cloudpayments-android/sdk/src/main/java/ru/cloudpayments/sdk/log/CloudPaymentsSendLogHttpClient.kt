package ru.cloudpayments.sdk.log

import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ru.cloudpayments.sdk.Constants
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object CloudPaymentsSendLogHttpClient {

    private const val apiUrl = Constants.fromtMonitoringApiUrl
    private const val apiKey = Constants.fromtMonitoringApiKey
    private const val loggerEndPoint = "api/monitoring/logger"

    private var osVersion: String = Build.VERSION.RELEASE
    private var manufacturer: String = Build.MANUFACTURER
    private var model: String = Build.MODEL

    private var publicId: String? = null

    fun setPublicId(id: String) {
        publicId = id
    }

    private val gson = Gson()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    fun sendErrorLog(message: String, stackTrace: String) {

        val encodedStackTrace = URLEncoder.encode(stackTrace, StandardCharsets.UTF_8.toString())

        val templateData = JsonArray().apply {
            add(publicId)
            add(message)
            add(encodedStackTrace)
            add(osVersion)
            add(model)
            add(manufacturer)
        }

        val jsonObject = JsonObject().apply {
            addProperty("apiKey", apiKey)
            addProperty("level", "ERROR")
            addProperty("messageTemplate", "Exception on user {publicId} with message {exceptionMessage} with {stackTrace}, on Android OS version {osVersion}, device model {model}, manufacturer {manufacturer}")
            add("templateData", templateData)
        }
        val jsonString = gson.toJson(jsonObject)

        sendLog(jsonString)
    }

    fun sendApiLog(method: String, url: String, statusCode: String) {

        val templateData = JsonArray().apply {
            add(publicId)
            add(method)
            add(url)
            add(statusCode)
            add(osVersion)
            add(model)
            add(manufacturer)
        }

        var jsonObject = JsonObject().apply {
            addProperty("apiKey", apiKey)
            addProperty("level", "INFO")
            addProperty(
                "messageTemplate",
                "User {publicId} send a request {method} {url} and received a response with status code {statusCode}, on Android OS version {osVersion}, device model {model}, manufacturer {manufacturer}"
            )
            add("templateData", templateData)
        }

        val jsonString = gson.toJson(jsonObject)
        sendLog(jsonString)
    }

    private fun sendLog(jsonBody: String) {
        val loggerUrl = apiUrl + loggerEndPoint
        sendPostRequest(loggerUrl, jsonBody)
    }

    private fun sendPostRequest(url: String, jsonBody: String) {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body: RequestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

            }
        })
    }
}