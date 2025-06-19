package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.cloudpayments.sdk.ui.dialogs.PaymentFinishStatus

internal class PaymentFinishViewModelFactory(
	val status: PaymentFinishStatus,
	val transactionId: Long? = null,
	val reasonCode: String? = null
): ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return PaymentFinishViewModel(status,
									  transactionId,
									  reasonCode) as T
	}
}