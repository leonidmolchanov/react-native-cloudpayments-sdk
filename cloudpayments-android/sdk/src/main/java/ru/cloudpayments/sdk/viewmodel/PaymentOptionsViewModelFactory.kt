package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cloudpayments.sdk.configuration.PaymentConfiguration

internal class PaymentOptionsViewModelFactory(
	private val paymentConfiguration: PaymentConfiguration
): ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return PaymentOptionsViewModel(paymentConfiguration) as T
	}
}