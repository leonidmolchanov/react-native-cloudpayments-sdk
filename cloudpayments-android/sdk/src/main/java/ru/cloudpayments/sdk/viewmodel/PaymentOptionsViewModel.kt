package ru.cloudpayments.sdk.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.cloudpayments.sdk.api.CloudpaymentsApi
import ru.cloudpayments.sdk.api.models.MirPayLinkBody
import ru.cloudpayments.sdk.api.models.SBPBanksItem
import ru.cloudpayments.sdk.api.models.SBPQrLinkBody
import ru.cloudpayments.sdk.api.models.QrPayLinkBody
import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.models.ApiError
import ru.cloudpayments.sdk.ui.dialogs.PaymentOptionsStatus
import ru.cloudpayments.sdk.util.checkAndGetCorrectJsonDataString
import javax.inject.Inject

internal class PaymentOptionsViewModel(
    private val paymentConfiguration: PaymentConfiguration
): BaseViewModel<PaymentOptionsViewState>() {
    override var currentState = PaymentOptionsViewState()
    override val viewState: MutableLiveData<PaymentOptionsViewState> by lazy {
        MutableLiveData(currentState)
    }

    private var disposable: Disposable? = null

    @Inject
    lateinit var api: CloudpaymentsApi

    fun getTQrPayLink(saveCard: Boolean?) {

        val jsonDataString: String? = checkAndGetCorrectJsonDataString(paymentConfiguration.paymentData.getJsonData())

        val body = QrPayLinkBody(amount = paymentConfiguration.paymentData.amount,
								 currency = paymentConfiguration.paymentData.currency,
								 description = paymentConfiguration.paymentData.description ?: "",
								 accountId = paymentConfiguration.paymentData.accountId ?: "",
								 email = paymentConfiguration.paymentData.email ?: "",
								 jsonData = jsonDataString,
								 invoiceId = paymentConfiguration.paymentData.invoiceId ?: "",
								 scheme = if (paymentConfiguration.useDualMessagePayment) "auth" else "charge")

        if (saveCard != null) {
            body.saveCard = saveCard
        }

        val state = currentState.copy(status = PaymentOptionsStatus.TPayLoading)
        stateChanged(state)

        disposable = api.getTPayQrLink(body)
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                val state = if (response.success == true) {
                    currentState.copy(status = PaymentOptionsStatus.TPaySuccess,
                                      qrUrl = response.transaction?.qrUrl,
                                      transactionId = response.transaction?.transactionId)
                } else {
                    currentState.copy(status = PaymentOptionsStatus.Failed, transactionId = response.transaction?.transactionId)
                }
                stateChanged(state)
            }
            .onErrorReturn {
                val state = currentState.copy(status = PaymentOptionsStatus.Failed, reasonCode = ApiError.CODE_ERROR_CONNECTION)
                stateChanged(state)
            }
            .subscribe()
    }

    fun getSberQrPayLink(saveCard: Boolean?) {

        val jsonDataString: String? = checkAndGetCorrectJsonDataString(paymentConfiguration.paymentData.getJsonData())

        val body = QrPayLinkBody(amount = paymentConfiguration.paymentData.amount,
                                 currency = paymentConfiguration.paymentData.currency,
                                 description = paymentConfiguration.paymentData.description ?: "",
                                 accountId = paymentConfiguration.paymentData.accountId ?: "",
                                 email = paymentConfiguration.paymentData.email ?: "",
                                 jsonData = jsonDataString,
                                 invoiceId = paymentConfiguration.paymentData.invoiceId ?: "",
                                 scheme = if (paymentConfiguration.useDualMessagePayment) "auth" else "charge")

        if (saveCard != null) {
            body.saveCard = saveCard
        }

        val state = currentState.copy(status = PaymentOptionsStatus.SberPayLoading)
        stateChanged(state)

        disposable = api.getSberPayQrLink(body)
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                val state = if (response.success == true) {
                    currentState.copy(status = PaymentOptionsStatus.SberPaySuccess,
                                      qrUrl = response.transaction?.qrUrl,
                                      transactionId = response.transaction?.transactionId)
                } else {
                    currentState.copy(status = PaymentOptionsStatus.Failed, transactionId = response.transaction?.transactionId)
                }
                stateChanged(state)
            }
            .onErrorReturn {
                val state = currentState.copy(status = PaymentOptionsStatus.Failed, reasonCode = ApiError.CODE_ERROR_CONNECTION)
                stateChanged(state)
            }
            .subscribe()
    }

    fun getSBPQrPayLink(saveCard: Boolean?) {

        val jsonDataString: String? = checkAndGetCorrectJsonDataString(paymentConfiguration.paymentData.getJsonData())

        val body = SBPQrLinkBody(amount = paymentConfiguration.paymentData.amount,
                                 currency = paymentConfiguration.paymentData.currency,
                                 description = paymentConfiguration.paymentData.description ?: "",
                                 accountId = paymentConfiguration.paymentData.accountId ?: "",
                                 email = paymentConfiguration.paymentData.email ?: "",
                                 jsonData = jsonDataString,
                                 invoiceId = paymentConfiguration.paymentData.invoiceId ?: "",
                                 scheme = if (paymentConfiguration.useDualMessagePayment) "auth" else "charge")

        if (saveCard != null) {
            body.saveCard = saveCard
        }

        val state = currentState.copy(status = PaymentOptionsStatus.SbpLoading)
        stateChanged(state)

        disposable = api.getSBPQrLink(body)
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                val state = if (response.success == true) {
                    currentState.copy(status = PaymentOptionsStatus.SbpSuccess,
                                      qrUrl = response.transaction?.qrUrl,
                                      providerQrId = response.transaction?.providerQrId,
                                      transactionId = response.transaction?.transactionId,
                                      listOfBanks = response.transaction?.banks?.dictionary)
                } else {
                    currentState.copy(status = PaymentOptionsStatus.Failed)
                }
                stateChanged(state)
            }
            .onErrorReturn {
                val state = currentState.copy(status = PaymentOptionsStatus.Failed, reasonCode = ApiError.CODE_ERROR_CONNECTION)
                stateChanged(state)
            }
            .subscribe()
    }

    fun getMirPayLink() {

        val body = MirPayLinkBody(amount = paymentConfiguration.paymentData.amount,
                                 currency = paymentConfiguration.paymentData.currency)

        val state = currentState.copy(status = PaymentOptionsStatus.MirPayLoading)
        stateChanged(state)

        disposable = api.getMirPayLink(body)
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                val state = if (response.success == true) {
                    currentState.copy(status = PaymentOptionsStatus.MirPaySuccess,
                                      mirPayDeepLink = response.mirPayLink?.deepLink,
                                      mirPayGuid = response.mirPayLink?.guid)
                } else {
                    currentState.copy(status = PaymentOptionsStatus.Failed)
                }
                stateChanged(state)
            }
            .onErrorReturn {
                val state = currentState.copy(status = PaymentOptionsStatus.Failed, reasonCode = ApiError.CODE_ERROR_CONNECTION)
                stateChanged(state)
            }
            .subscribe()
    }

    private fun stateChanged(viewState: PaymentOptionsViewState) {
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

internal data class PaymentOptionsViewState(
    val status: PaymentOptionsStatus = PaymentOptionsStatus.Waiting,
    val reasonCode: String? = null,
    val qrUrl: String? = null,
    val providerQrId: String? = null,
    val transactionId: Long? = null,
    val listOfBanks: ArrayList<SBPBanksItem>? = null,
    val isSaveCard: Int? = null,
    val mirPayDeepLink: String? = null,
    val mirPayGuid: String? = null
): BaseViewState()