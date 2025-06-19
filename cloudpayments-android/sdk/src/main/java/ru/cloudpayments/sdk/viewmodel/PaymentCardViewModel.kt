package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.cloudpayments.sdk.api.CloudpaymentsApi
import ru.cloudpayments.sdk.util.getSha512
import javax.inject.Inject

internal class PaymentCardViewModel: BaseViewModel<PaymentCardViewState>() {
	override var currentState = PaymentCardViewState()
	override val viewState: MutableLiveData<PaymentCardViewState> by lazy {
		MutableLiveData(currentState)
	}

	private var disposable: Disposable? = null

	@Inject lateinit var api: CloudpaymentsApi

	fun getBinInfo(cardNumber: String, amount: String, currency: String, isAllowedNotSanctionedCards: Boolean, isQiwi: Boolean) {

		if (cardNumber.length < 6) {
			return
		}
		val queryMap = mutableMapOf<String, String>()

		val bin = cardNumber.substring(0, 6)
		queryMap["bin"] = bin

		queryMap["amount"] = amount
		queryMap["currency"] = currency
		queryMap["isAllowedNotSanctionedCards"] = isAllowedNotSanctionedCards.toString()
		queryMap["isQiwi"] = isQiwi.toString()

		if (cardNumber.length >= 7) {
			queryMap["sevenNumberHash"] = getSha512(cardNumber.substring(0, 7))
		}

		if (cardNumber.length >= 8) {
			queryMap["eightNumberHash"] = getSha512(cardNumber.substring(0, 8))
		}

		disposable = api.getBinInfo(queryMap)
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map {
				if (it.success) {
					it.binInfo?.hideCvv?.let { hideCvv ->
						val state: PaymentCardViewState = currentState.copy(isCvvRequired = !hideCvv)
						stateChanged(state)
					}
				} else {
					// leave it
				}
			}
			.onErrorReturn {
				// leave it
			}
			.subscribe()
	}

	override fun onCleared() {
		super.onCleared()

		disposable?.dispose()
	}

	private fun stateChanged(viewState: PaymentCardViewState) {
		currentState = viewState.copy()
		this.viewState.apply {
			value = viewState
		}
	}
}

internal data class PaymentCardViewState(
	val isCvvRequired: Boolean = true
): BaseViewState()