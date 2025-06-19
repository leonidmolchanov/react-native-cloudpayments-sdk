package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName

data class CloudPaymentsGetMirPayCryptogramResponse(
		@SerializedName("Success") val success: Boolean,
		@SerializedName("Message") val message: String?,
		@SerializedName("Model") val cryptogram: Cryptogram?)

data class Cryptogram(
	@SerializedName("Cryptogram") val cryptogram: String?)


