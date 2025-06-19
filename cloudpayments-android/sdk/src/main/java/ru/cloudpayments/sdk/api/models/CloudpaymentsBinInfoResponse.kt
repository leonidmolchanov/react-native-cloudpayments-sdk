package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName

data class CloudpaymentsBinInfoResponse(
		@SerializedName("Success") val success: Boolean,
		@SerializedName("Message") val message: String?,
		@SerializedName("Model") val binInfo: CloudpaymentsBinInfo?)

data class CloudpaymentsBinInfo(
		@SerializedName("LogoUrl") val logoUrl: String?,
		@SerializedName("BankName") val bankName: String?,
		@SerializedName("CardType") val cardType: String?,
		@SerializedName("ConvertedAmount") val convertedAmount: String?,
		@SerializedName("Currency") val currency: String?,
		@SerializedName("HideCvvInput") val hideCvv: Boolean)
