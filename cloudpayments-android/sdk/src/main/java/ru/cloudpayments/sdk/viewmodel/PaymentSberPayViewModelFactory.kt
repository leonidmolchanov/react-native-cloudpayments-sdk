package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cloudpayments.sdk.configuration.PaymentConfiguration

internal class PaymentSberPayViewModelFactory(
	private val qrUrl: String,
	private val transactionId: Long,
	private val paymentConfiguration: PaymentConfiguration,
	private val saveCard: Boolean?
): ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return PaymentSberPayViewModel(qrUrl, transactionId, paymentConfiguration, saveCard) as T
	}
}