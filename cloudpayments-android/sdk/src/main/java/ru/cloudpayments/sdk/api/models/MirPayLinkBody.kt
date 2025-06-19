package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName
data class MirPayLinkBody(
	@SerializedName("amount") val amount: String, // Сумма
	@SerializedName("currency") val currency: String) // Валюта
