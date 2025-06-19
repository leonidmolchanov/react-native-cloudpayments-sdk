package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import ru.cloudpayments.sdk.api.CloudpaymentsApi
import ru.cloudpayments.sdk.card.Card
import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.ui.dialogs.PaymentMirPayStatus
import javax.inject.Inject

internal class PaymentMirPayViewModel(
	private val deeplink: String,
	private val guid: String,
	private val paymentConfiguration: PaymentConfiguration
): BaseViewModel<PaymentMirPayViewState>() {
	override var currentState = PaymentMirPayViewState()
	override val viewState: MutableLiveData<PaymentMirPayViewState> by lazy {
		MutableLiveData(currentState)
	}

	private var disposable: Disposable? = null

	@Inject
	lateinit var api: CloudpaymentsApi

	fun getCryptogram(guid: String) {

		disposable = api.getMirPayCryptogram(guid)
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map { response ->
				if (response.success) {
					if (response.cryptogram?.cryptogram.isNullOrBlank()) {
						getCryptogram(guid)
					} else {

						val mirPayCryptogram = JSONObject(response.cryptogram?.cryptogram)
						val cryptogram = mirPayCryptogram.getString("Cryptogram")

						val state = currentState.copy(
							status = PaymentMirPayStatus.Succeeded,
							cryptogram = response.cryptogram?.cryptogram?.let {
								Card.createMirPayHexPacketFromCryptogram(
									it
								)
							}
						)
						stateChanged(state)
					}

				} else {
					val state = currentState.copy(status = PaymentMirPayStatus.Failed)
					stateChanged(state)
				}
			}
			.onErrorReturn {
				val state = currentState.copy(status = PaymentMirPayStatus.Failed)
				stateChanged(state)
			}
			.subscribe()
	}

	private fun stateChanged(viewState: PaymentMirPayViewState) {
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

internal data class PaymentMirPayViewState(
	val status: PaymentMirPayStatus = PaymentMirPayStatus.InProcess,
	val succeeded: Boolean = false,
	val cryptogram: String? = null,
	val errorMessage: String? = null,
	val reasonCode: Int? = null
): BaseViewState()