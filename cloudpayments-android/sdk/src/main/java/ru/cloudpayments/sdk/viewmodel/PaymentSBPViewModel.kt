package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.cloudpayments.sdk.api.CloudpaymentsApi
import ru.cloudpayments.sdk.api.models.CloudpaymentsTransaction
import ru.cloudpayments.sdk.api.models.QrLinkStatusWaitBody
import ru.cloudpayments.sdk.api.models.QrLinkStatusWaitResponse
import ru.cloudpayments.sdk.api.models.SBPBanksItem
import ru.cloudpayments.sdk.configuration.PaymentData
import ru.cloudpayments.sdk.ui.dialogs.PaymentSBPStatus
import javax.inject.Inject

internal class PaymentSBPViewModel(
	private val paymentData: PaymentData,
	private val useDualMessagePayment: Boolean,
	private val saveCard: Boolean?
): BaseViewModel<PaymentSBPViewState>() {
	override var currentState = PaymentSBPViewState()
	override val viewState: MutableLiveData<PaymentSBPViewState> by lazy {
		MutableLiveData(currentState)
	}

	private var disposable: Disposable? = null

	@Inject
	lateinit var api: CloudpaymentsApi

	fun qrLinkStatusWait(transactionId: Long?) {

		if (transactionId == null || transactionId == 0L) {
			val state = currentState.copy(status = PaymentSBPStatus.Failed)
			stateChanged(state)
			return
		}

		val body = QrLinkStatusWaitBody(transactionId)

		disposable = api.qrLinkStatusWait(body)
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map { response ->
				checkQrLinkStatusWaitResponse(response)
			}
			.onErrorReturn {
				val state = currentState.copy(status = PaymentSBPStatus.Failed)
				stateChanged(state)
			}
			.subscribe()
	}

	private fun checkQrLinkStatusWaitResponse(response: QrLinkStatusWaitResponse) {

		if (response.success == true) {
			when (response.transaction?.status) {
				"Authorized", "Completed", "Cancelled" -> {
					val state = currentState.copy(status = PaymentSBPStatus.Succeeded, transactionId = response.transaction.transactionId)
					stateChanged(state)
				}
				"Declined" -> {
					val state = currentState.copy(status = PaymentSBPStatus.Failed, transactionId = response.transaction.transactionId)
					stateChanged(state)
				}
				else -> {
					qrLinkStatusWait(response.transaction?.transactionId)
				}
			}

		} else {
			val state = currentState.copy(status = PaymentSBPStatus.Failed, transactionId = response.transaction?.transactionId)
			stateChanged(state)
		}
	}

	private fun stateChanged(viewState: PaymentSBPViewState) {

		currentState = viewState.copy()
		this.viewState.apply {
			value = viewState
		}
	}

	override fun onCleared() {
		super.onCleared()
		disposable?.dispose()
	}
}

internal data class PaymentSBPViewState(
	val status: PaymentSBPStatus = PaymentSBPStatus.ListOfBanks,
	val succeeded: Boolean = false,
	val transaction: CloudpaymentsTransaction? = null,
	val errorMessage: String? = null,
	val reasonCode: Int? = null,
	val qrUrl: String? = null,
	val transactionId: Long? = null,
	var listOfBanks: List<SBPBanksItem>? = null

): BaseViewState()