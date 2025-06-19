package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable

data class CloudpaymentsGetQrPayLinkResponse(
	@SerializedName("Success") val success: Boolean?,
	@SerializedName("Message") val message: String?,
	@SerializedName("Model") val transaction: CloudpaymentsQrPayLinkTransaction?) {
	fun handleError(): Observable<CloudpaymentsGetQrPayLinkResponse> {
		return if (success == true ) {
			Observable.just(this)
		} else {
			Observable.error(CloudpaymentsTransactionError(message ?: ""))
		}
	}
}