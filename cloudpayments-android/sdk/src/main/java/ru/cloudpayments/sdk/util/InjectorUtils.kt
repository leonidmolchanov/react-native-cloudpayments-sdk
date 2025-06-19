package ru.cloudpayments.sdk.util

import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.configuration.PaymentData
import ru.cloudpayments.sdk.ui.dialogs.PaymentFinishStatus
import ru.cloudpayments.sdk.viewmodel.PaymentFinishViewModelFactory
import ru.cloudpayments.sdk.viewmodel.PaymentMirPayViewModelFactory
import ru.cloudpayments.sdk.viewmodel.PaymentOptionsViewModelFactory
import ru.cloudpayments.sdk.viewmodel.PaymentProcessViewModelFactory
import ru.cloudpayments.sdk.viewmodel.PaymentSBPViewModelFactory
import ru.cloudpayments.sdk.viewmodel.PaymentSberPayViewModelFactory
import ru.cloudpayments.sdk.viewmodel.PaymentTPayViewModelFactory

internal object InjectorUtils {

    fun providePaymentOptionsViewModelFactory(paymentConfiguration: PaymentConfiguration): PaymentOptionsViewModelFactory {
        return PaymentOptionsViewModelFactory(paymentConfiguration)
    }
    fun providePaymentProcessViewModelFactory(paymentData: PaymentData, cryptogram: String, useDualMessagePayment: Boolean, saveCard: Boolean?): PaymentProcessViewModelFactory {
        return PaymentProcessViewModelFactory(paymentData, cryptogram, useDualMessagePayment, saveCard)
    }

    fun providePaymentTPayViewModelFactory(qrUrl: String, transactionId: Long, paymentConfiguration: PaymentConfiguration, saveCard: Boolean?): PaymentTPayViewModelFactory {
        return PaymentTPayViewModelFactory(qrUrl, transactionId, paymentConfiguration, saveCard)
    }

    fun providePaymentSberPayViewModelFactory(qrUrl: String, transactionId: Long, paymentConfiguration: PaymentConfiguration, saveCard: Boolean?): PaymentSberPayViewModelFactory {
        return PaymentSberPayViewModelFactory(qrUrl, transactionId, paymentConfiguration, saveCard)
    }

    fun providePaymentMirPayViewModelFactory(deepLink: String, guid: String, paymentConfiguration: PaymentConfiguration): PaymentMirPayViewModelFactory {
        return PaymentMirPayViewModelFactory(deepLink, guid, paymentConfiguration)
    }

    fun providePaymentSBPViewModelFactory(paymentData: PaymentData, useDualMessagePayment: Boolean, saveCard: Boolean?): PaymentSBPViewModelFactory {
        return PaymentSBPViewModelFactory(paymentData, useDualMessagePayment, saveCard)
    }

    fun providePaymentFinishViewModelFactory(status: PaymentFinishStatus,
                                             transactionId: Long?,
                                             reasonCode: String?): PaymentFinishViewModelFactory {
        return PaymentFinishViewModelFactory(status,
                                             transactionId,
                                             reasonCode)
    }
}