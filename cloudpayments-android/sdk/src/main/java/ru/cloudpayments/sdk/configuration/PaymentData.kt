package ru.cloudpayments.sdk.configuration

import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject
import ru.cloudpayments.sdk.api.models.PaymentDataPayer
import ru.cloudpayments.sdk.api.models.PaymentDataReceipt
import ru.cloudpayments.sdk.api.models.PaymentDataReceiptItem
import ru.cloudpayments.sdk.api.models.PaymentDataRecurrent
import ru.cloudpayments.sdk.api.models.Split
import ru.cloudpayments.sdk.util.TAG

@Parcelize
class PaymentData(
	val amount: String,
	var currency: String = "RUB",
	val invoiceId: String? = null,
	val description: String? = null,
	val accountId: String? = null,
	var email: String? = null,
	val payer: PaymentDataPayer? = null,
	val splits: List<Split>? = null,
	val recurrent: PaymentDataRecurrent? = null,
	val receipt: PaymentDataReceipt? = null,
	private val jsonData: String? = null
) : Parcelable {

	fun jsonDataHasRecurrent(): Boolean {
		if (!jsonData.isNullOrEmpty()) {
			val gson = GsonBuilder()
				.setLenient()
				.create()

			try {
				val cpJsonData = gson.fromJson(jsonData, CpJsonData::class.java)
				cpJsonData.cloudPayments?.recurrent?.interval?.let {
					return true
				}
			} catch (e: JsonSyntaxException) {
				Log.e(TAG, "JsonData syntax error")
			}
		}
		return false
	}

	fun jsonDataHasRecurrentOrReceipt(): Boolean {
		if (!jsonData.isNullOrEmpty()) {
			if (jsonData.toLowerCase().contains("cloudpayments")) {
					if (jsonData.toLowerCase().contains("recurrent") ||
						jsonData.toLowerCase().contains("customerreceipt")) {
						return true
					}
				}
		}
		return false
	}

	fun getJsonData(): String? {

		if (jsonDataHasRecurrentOrReceipt()) {
			return jsonData
		}

		var baseData = mutableMapOf<String, Any>()

		val existingJsonData = this.jsonData
		if (!existingJsonData.isNullOrEmpty()) {
			convertStringToDictionary(existingJsonData)?.let {
				baseData = it.toMutableMap()
			}
		}

		val cloudPayments = (baseData["cloudPayments"] as? Map<String, Any>)?.toMutableMap() ?: mutableMapOf()

		recurrent?.let {
			cloudPayments["recurrent"] = recurrent
		}

		receipt?.let {
			cloudPayments["customerReceipt"] = receipt
		}

		if (cloudPayments.isNotEmpty()) {
			baseData["cloudPayments"] = cloudPayments
		}

		return try {
			val encoder = Gson()
			val jsonData = encoder.toJsonTree(baseData).asJsonObject.toString()
			jsonData
		} catch (e: JSONException) {
			println("Failed to serialize JSON: ${e.localizedMessage}")
			null
		}
	}

	fun convertStringToDictionary(jsonString: String): Map<String, Any>? {
		return try {
			jsonObjectToMap(JSONObject(jsonString))
		} catch (e: JSONException) {
			println("Failed to parse JSON string to Map: ${e.localizedMessage}")
			null
		}
	}

	fun jsonObjectToMap(jObject: JSONObject): Map<String, Any> {
		val map = mutableMapOf<String, Any>()
		val keys = jObject.keys()
		while (keys.hasNext()) {
			val key = keys.next()
			map[key] = jObject.get(key)
		}
		return map
	}
}

data class CpJsonData(
	@SerializedName("cloudPayments") val cloudPayments: CloudPaymentsJsonData?)

data class CloudPaymentsJsonData(
	@SerializedName("recurrent") val recurrent: CloudPaymentsRecurrentJsonData?)

data class CloudPaymentsRecurrentJsonData(
	@SerializedName("interval") val interval: String?,
	@SerializedName("period") val period: String?,
	@SerializedName("amount") val amount: String?)


