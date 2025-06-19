package ru.cloudpayments.sdk.log

import okhttp3.Interceptor
import okhttp3.Response

class CloudPaymentsLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        val response = chain.proceed(request)
        val statusCode = response.code

        CloudPaymentsSendLogHttpClient.sendApiLog(method, url, statusCode.toString())
        return response
    }
}