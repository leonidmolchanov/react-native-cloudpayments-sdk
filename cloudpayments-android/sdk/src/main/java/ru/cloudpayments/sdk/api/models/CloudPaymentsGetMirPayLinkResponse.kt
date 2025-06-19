package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable

data class CloudPaymentsGetMirPayLinkResponse(
	@SerializedName("Success") val success: Boolean?,
	@SerializedName("Message") val message: String?,
	@SerializedName("Model") val mirPayLink: CloudPaymentsMirPayLink?) {
	fun handleError(): Observable<CloudPaymentsGetMirPayLinkResponse> {
		return if (success == true ) {
			Observable.just(this)
		} else {
			Observable.error(CloudpaymentsTransactionError(message ?: ""))
		}
	}
}

data class CloudPaymentsMirPayLink(
	@SerializedName("DeepLink") val deepLink: String,
	@SerializedName("Guid") val guid: String)