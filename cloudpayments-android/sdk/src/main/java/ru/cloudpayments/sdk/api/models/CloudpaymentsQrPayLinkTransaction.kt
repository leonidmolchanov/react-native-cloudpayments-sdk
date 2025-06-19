package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName

data class CloudpaymentsQrPayLinkTransaction(
	@SerializedName("TransactionId") val transactionId: Long?,
	@SerializedName("ProviderQrId") val providerQrId: String?,
	@SerializedName("QrUrl") val qrUrl: String?)