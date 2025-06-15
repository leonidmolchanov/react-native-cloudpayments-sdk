package com.cloudpaymentssdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * Event Emitter для отправки событий CloudPayments в JavaScript
 *
 * @description Класс для отправки событий из нативного Android кода в React Native.
 * Аналогичен iOS EventEmitter, обеспечивает совместимость с JS кодом.
 *
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */
class CloudPaymentsEventEmitter(private val reactContext: ReactApplicationContext) {
    
    /**
     * Отправка произвольного события в JavaScript
     *
     * @param eventName Название события
     * @param params Параметры события (опционально)
     */
    fun sendEvent(eventName: String, params: WritableMap?) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
        }
    }
    
    /**
     * Отправка события платежной формы
     *
     * @description Отправляет событие PaymentForm с указанным действием.
     * Совместимо с iOS версией и JS обработчиками.
     *
     * @param action Действие: 'willDisplay', 'didDisplay', 'willHide', 'didHide', 'transaction'
     * @param statusCode Код статуса (только для action='transaction')
     * @param transactionId ID транзакции (только для успешных транзакций)
     * @param message Сообщение (только для ошибок транзакций)
     * @param errorCode Код ошибки (только для ошибок транзакций)
     */
    fun sendPaymentFormEvent(
        action: String,
        statusCode: Boolean? = null,
        transactionId: Long? = null,
        message: String? = null,
        errorCode: String? = null
    ) {
        val params = Arguments.createMap().apply {
            putString(EPaymentFormAction.ACTION.rawValue, action)
            
            // Для транзакций добавляем дополнительные поля
            if (action == EPaymentFormAction.TRANSACTION.rawValue) {
                statusCode?.let { putBoolean(EResponseKeys.STATUS_CODE.rawValue, it) }
                transactionId?.let { putDouble(EResponseKeys.TRANSACTION_ID.rawValue, it.toDouble()) }
                message?.let { putString(EResponseKeys.MESSAGE.rawValue, it) }
                errorCode?.let { putString(EResponseKeys.ERROR_CODE.rawValue, it) }
            }
        }
        
        sendEvent(EPaymentFormEventName.PAYMENT_FORM.rawValue, params)
    }
    
    /**
     * Отправка события успешной транзакции
     */
    fun sendTransactionSuccess(transactionId: Long, message: String? = null) {
        sendPaymentFormEvent(
            action = EPaymentFormAction.TRANSACTION.rawValue,
            statusCode = true,
            transactionId = transactionId,
            message = message
        )
    }
    
    /**
     * Отправка события ошибки транзакции
     */
    fun sendTransactionError(message: String, errorCode: String? = null) {
        sendPaymentFormEvent(
            action = EPaymentFormAction.TRANSACTION.rawValue,
            statusCode = false,
            message = message,
            errorCode = errorCode
        )
    }
    
    /**
     * Отправка события отображения формы
     */
    fun sendFormWillDisplay() {
        sendPaymentFormEvent(EPaymentFormEvent.WILL_DISPLAY.rawValue)
    }
    
    /**
     * Отправка события отображенной формы
     */
    fun sendFormDidDisplay() {
        sendPaymentFormEvent(EPaymentFormEvent.DID_DISPLAY.rawValue)
    }
    
    /**
     * Отправка события скрытия формы
     */
    fun sendFormWillHide() {
        sendPaymentFormEvent(EPaymentFormEvent.WILL_HIDE.rawValue)
    }
    
    /**
     * Отправка события скрытой формы
     */
    fun sendFormDidHide() {
        sendPaymentFormEvent(EPaymentFormEvent.DID_HIDE.rawValue)
    }
} 