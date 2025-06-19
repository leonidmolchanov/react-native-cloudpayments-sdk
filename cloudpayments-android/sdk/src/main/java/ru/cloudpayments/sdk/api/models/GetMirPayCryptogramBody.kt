package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName
data class GetMirPayCryptogramBody(
	@SerializedName("Guid") val guid: String)
