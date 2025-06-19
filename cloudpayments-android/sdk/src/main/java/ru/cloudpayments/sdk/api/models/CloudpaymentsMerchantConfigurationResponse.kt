package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName

data class CloudpaymentsMerchantConfigurationResponse(
	@SerializedName("Success") val success: Boolean?,
	@SerializedName("Message") val message: String?,
	@SerializedName("Model") val model: MerchantConfiguration?
)

data class MerchantConfiguration(
	@SerializedName("TerminalFullUrl") val terminalFullUrl: String?,
	@SerializedName("ExternalPaymentMethods") val externalPaymentMethods: ArrayList<ExternalPaymentMethods>?,
	@SerializedName("Features") val features: Features?,
	@SerializedName("IsCvvRequired") val isCvvRequired: Boolean?,
	@SerializedName("SkipExpiryValidation") val skipExpiryValidation: Boolean?
)

data class ExternalPaymentMethods(


	@SerializedName("Type") val type: Int?,
	@SerializedName("Enabled") val enabled: Boolean?,
	@SerializedName("GPayGatewayName") val gPayGatewayName: String?
) {
	companion object {
		val APPLE_PAY = 0
		val GOOGLE_PAY = 1
		val MASTER_PASS = 2
		val YANDEX_PAY = 3
		val TCS_CREDIT = 4
		val SBP = 5
		val T_PAY = 6
		val MIR_PAY = 7
		val SPEI = 8
		val DOLYAME = 9
		val MTS_PAY = 10
		val SOM = 11
		val SBER_PAY = 12
	}
}

data class Features(
	@SerializedName("IsSaveCard") val isSaveCard: Int?,
	@SerializedName("IsAllowedNotSanctionedCards") val isAllowedNotSanctionedCards: Boolean = true,
	@SerializedName("IsQiwi") val isQiwi: Boolean = false
)