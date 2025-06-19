package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.MutableLiveData
import ru.cloudpayments.sdk.ui.dialogs.PaymentFinishStatus

internal class PaymentFinishViewModel(
	val status: PaymentFinishStatus,
	val transactionId: Long? = null,
	val reasonCode: String? = null
): BaseViewModel<PaymentFinishViewState>() {
	override var currentState = PaymentFinishViewState()
	override val viewState: MutableLiveData<PaymentFinishViewState> by lazy {
		MutableLiveData(currentState)
	}

	private fun stateChanged(viewState: PaymentFinishViewState) {
		currentState = viewState.copy()
		this.viewState.apply {
			value = viewState
		}
	}
}

internal data class PaymentFinishViewState(
	val status: PaymentFinishStatus = PaymentFinishStatus.Failed,
	val transactionId: Long? = null,
	val reasonCode: String? = null
): BaseViewState()