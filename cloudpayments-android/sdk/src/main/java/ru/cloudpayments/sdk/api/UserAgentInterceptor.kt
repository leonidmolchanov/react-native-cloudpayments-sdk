package ru.cloudpayments.sdk.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class UserAgentInterceptor(private val userAgent: String) : Interceptor {

	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response {
		val original: Request = chain.request()

		val requestWithUserAgent = original.newBuilder()
			.header("User-Agent", userAgent)
			.build()

		for (i in 0 until requestWithUserAgent.headers.size) {
			Log.i("OkHttp",
				String.format(
					"%s: %s",
					requestWithUserAgent.headers.name(i),
					requestWithUserAgent.headers.value(i)
				)
			)
		}

		return chain.proceed(requestWithUserAgent)
	}
}
