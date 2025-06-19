package ru.cloudpayments.sdk.api.models

import com.google.gson.annotations.SerializedName
import ru.cloudpayments.sdk.Constants

data class SBPQrLinkBody(
	@SerializedName("Webview") val webView: Boolean = true, // Мобильное устройство
	@SerializedName("Device") val device: String = "MobileApp", // Вызов из мобильных приложений
	@SerializedName("Amount") val amount: String, // Сумма
	@SerializedName("Currency") val currency: String, // Валюта
	@SerializedName("Description") val description: String? = null, // Описание платежа
	@SerializedName("AccountId") val accountId: String? = null, // Identity плательщика в системе мерчанта
	@SerializedName("Email") val email: String? = null, // E-mail плательщика
	@SerializedName("JsonData") val jsonData: String? = null, // Произвольные данные мерчанта в формате JSON
	@SerializedName("InvoiceId") val invoiceId: String? = null, // id заказа в системе мерчанта
	@SerializedName("Scheme") val scheme: String, // charge - одностадийная оплата, auth - двухстадийная оплата (Scheme":"0")
	@SerializedName("TtlMinutes") val ttlMinutes: Int = 30, // Время жизни Qr
	@SerializedName("SuccessRedirectUrl") val successRedirectUrl: String = Constants.deeplinkToSdk, // Url успешной оплаты (мерчанта)
	@SerializedName("FailRedirectUrl") val failRedirectUrl: String = Constants.deeplinkToSdk, // Url неуспешной оплаты (мерчанта)
	@SerializedName("SaveCard") var saveCard: Boolean? = null,
	@SerializedName("Scenario") val scenario: Int = 7)