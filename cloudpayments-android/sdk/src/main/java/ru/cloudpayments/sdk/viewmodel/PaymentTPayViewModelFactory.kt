package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cloudpayments.sdk.configuration.PaymentConfiguration

internal class PaymentTPayViewModelFactory(
	private val qrUrl: String,
	private val transactionId: Long,
	private val paymentConfiguration: PaymentConfiguration,
	private val saveCard: Boolean?
): ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return PaymentTPayViewModel(qrUrl, transactionId, paymentConfiguration, saveCard) as T
	}
}