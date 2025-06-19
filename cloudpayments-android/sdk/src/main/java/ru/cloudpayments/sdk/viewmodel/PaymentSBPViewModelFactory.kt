package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cloudpayments.sdk.configuration.PaymentData

internal class PaymentSBPViewModelFactory(
	private val paymentData: PaymentData,
	private val useDualMessagePayment: Boolean,
	private val saveCard: Boolean?
): ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return PaymentSBPViewModel(paymentData, useDualMessagePayment, saveCard) as T
	}
}