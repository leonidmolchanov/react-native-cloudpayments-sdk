package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable

data class CloudpaymentsGetSBPQrLinkResponse(
	@SerializedName("Success") val success: Boolean?,
	@SerializedName("Message") val message: String?,
	@SerializedName("Model") val transaction: CloudpaymentsSBPQrLinkTransaction?) {
	fun handleError(): Observable<CloudpaymentsGetSBPQrLinkResponse> {
		return if (success == true ) {
			Observable.just(this)
		} else {
			Observable.error(CloudpaymentsTransactionError(message ?: ""))
		}
	}
}