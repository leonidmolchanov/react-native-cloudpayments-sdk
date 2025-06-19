package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.cloudpayments.sdk.api.CloudpaymentsApi
import ru.cloudpayments.sdk.api.models.CloudpaymentsTransaction
import ru.cloudpayments.sdk.api.models.QrLinkStatusWaitBody
import ru.cloudpayments.sdk.api.models.QrLinkStatusWaitResponse
import ru.cloudpayments.sdk.api.models.QrPayLinkBody
import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.ui.dialogs.PaymentTPayStatus
import ru.cloudpayments.sdk.util.checkAndGetCorrectJsonDataString
import javax.inject.Inject

internal class PaymentTPayViewModel(
	private val qrUrl: String,
	private val transactionId: Long,
	private val paymentConfiguration: PaymentConfiguration,
	private val saveCard: Boolean?
): BaseViewModel<PaymentTPayViewState>() {
	override var currentState = PaymentTPayViewState()
	override val viewState: MutableLiveData<PaymentTPayViewState> by lazy {
		MutableLiveData(currentState)
	}

	private var disposable: Disposable? = null

	@Inject
	lateinit var api: CloudpaymentsApi

	fun getTQrPayLink() {

		val jsonDataString: String? = checkAndGetCorrectJsonDataString(paymentConfiguration.paymentData.getJsonData())

		val body = QrPayLinkBody(amount = paymentConfiguration.paymentData.amount,
								 currency = paymentConfiguration.paymentData.currency,
								 description = paymentConfiguration.paymentData.description ?: "",
								 accountId = paymentConfiguration.paymentData.accountId ?: "",
								 email = paymentConfiguration.paymentData.email ?: "",
								 jsonData = jsonDataString,
								 invoiceId = paymentConfiguration.paymentData.invoiceId ?: "",
								 scheme = if (paymentConfiguration.useDualMessagePayment) "auth" else "charge")

		if (paymentConfiguration?.saveCardForSinglePaymentMode != null) {
			body.saveCard = paymentConfiguration?.saveCardForSinglePaymentMode
		}

		disposable = api.getTPayQrLink(body)
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map { response ->
				val state = if (response.success == true) {
					currentState.copy(status = PaymentTPayStatus.InProcess, qrUrl = response.transaction?.qrUrl,
									  transactionId = response.transaction?.transactionId)
				} else {
					currentState.copy(status = PaymentTPayStatus.Failed, transactionId = response.transaction?.transactionId)
				}
				stateChanged(state)
			}
			.onErrorReturn {
				var state = currentState.copy(status = PaymentTPayStatus.Failed)
				stateChanged(state)
			}
			.subscribe()
	}

	fun qrLinkStatusWait(transactionId: Long?) {

		val body = QrLinkStatusWaitBody(transactionId ?: 0)

		disposable = api.qrLinkStatusWait(body)
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map { response ->
				checkQrLinkStatusWaitResponse(response)
			}
			.onErrorReturn {
				val state = currentState.copy(status = PaymentTPayStatus.Failed)
				stateChanged(state)
			}
			.subscribe()
	}

	private fun checkQrLinkStatusWaitResponse(response: QrLinkStatusWaitResponse) {

		if (response.success == true) {
			when (response.transaction?.status) {
				"Authorized", "Completed", "Cancelled" -> {
					val state = currentState.copy(status = PaymentTPayStatus.Succeeded, transactionId = response.transaction?.transactionId)
					stateChanged(state)
				}
				"Declined" -> {
					val state = currentState.copy(status = PaymentTPayStatus.Failed, transactionId = response.transaction?.transactionId)
					stateChanged(state)
				}
				else -> {
					qrLinkStatusWait(response.transaction?.transactionId)
				}
			}

		} else {
			val state = currentState.copy(status = PaymentTPayStatus.Failed)
			stateChanged(state)
		}
	}

	private fun stateChanged(viewState: PaymentTPayViewState) {
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

internal data class PaymentTPayViewState(
	val status: PaymentTPayStatus = PaymentTPayStatus.InProcess,
	val succeeded: Boolean = false,
	val qrUrl: String? = null,
	val transactionId: Long? = null,
	val transaction: CloudpaymentsTransaction? = null,
	val errorMessage: String? = null,
	val reasonCode: Int? = null
): BaseViewState()