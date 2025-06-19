package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cloudpayments.sdk.configuration.PaymentConfiguration

internal class PaymentMirPayViewModelFactory(
	private val deepLink: String,
	private val guid: String,
	private val paymentConfiguration: PaymentConfiguration
): ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return PaymentMirPayViewModel(deepLink, guid, paymentConfiguration) as T
	}
}