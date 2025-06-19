package ru.cloudpayments.sdk.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.security.MessageDigest
import java.util.*

open class TextWatcherAdapter: TextWatcher {
	override fun afterTextChanged(s: Editable?) {}

	override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

	override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}

fun emailIsValid(email: String?): Boolean {
	if (email.isNullOrBlank()) {
		return false
	}
	return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun checkAndGetCorrectJsonDataString(json: String?): String? {
	return try {
		val parser = JsonParser()
		val jsonElement = parser.parse(json)
		Gson().toJson(jsonElement)
	} catch (e: JsonSyntaxException) {
		Log.e("CloudPaymentsSDK", "JsonSyntaxException in JsonData")
		null
	} catch (e: NullPointerException) {
		Log.e("CloudPaymentsSDK", "NullPointerException in JsonData")
		null
	}
}

fun getRussianLocale() = Locale("ru", "RU")

fun getSha512(input: String): String {
	return MessageDigest.getInstance("SHA-512")
		.digest(input.toByteArray())
		.joinToString(separator = "") {
			((it.toInt() and 0xff) + 0x100)
				.toString(16)
				.substring(1)
		}
}