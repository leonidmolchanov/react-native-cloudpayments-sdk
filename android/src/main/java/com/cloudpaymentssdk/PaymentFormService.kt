package com.cloudpaymentssdk

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.configuration.PaymentConfiguration

/**
 * Сервис для работы с платежной формой CloudPayments
 *
 * @description Класс для управления платежной формой Android SDK.
 * Аналогичен iOS PaymentFormService, но адаптирован под Android архитектуру.
 *
 * @author Leonid Molchanov
 * @since 1.0.0
 */
class PaymentFormService(
    private val reactContext: ReactApplicationContext,
    private val eventEmitter: CloudPaymentsEventEmitter
) {

    private var currentPublicId: String? = null
    private var paymentLauncher: ActivityResultLauncher<PaymentConfiguration>? = null
    private var currentPromise: PromiseWrapper? = null

    /**
     * Установка публичного ID мерчанта
     *
     * @param publicId Публичный идентификатор из личного кабинета CloudPayments
     */
    fun setPublicId(publicId: String) {
        currentPublicId = publicId
    }

    /**
     * Показ платежной формы CloudPayments
     *
     * @description Основной метод для запуска платежной формы.
     * Аналогичен iOS версии, возвращает Promise с результатом транзакции.
     *
     * @param paymentData Данные платежа из JavaScript
     * @return Promise с результатом операции
     *
     * @example Использование из JavaScript
     * ```javascript
     * const result = await PaymentService.presentPaymentForm({
     *   amount: '1000.00',
     *   currency: 'RUB',
     *   description: 'Покупка товара',
     *   email: 'user@example.com'
     * });
     * ```
     */
    fun presentPaymentForm(paymentData: ReadableMap, promise: Promise) {
        try {
            val publicId = currentPublicId
            if (publicId == null) {
                promise.reject(ECloudPaymentsError.SERVICE_UNINITIALIZED.rawValue, EDefaultMessages.PAYMENT_SERVICE_NOT_INITIALIZED.rawValue, null)
                return
            }

            val activity = reactContext.currentActivity
            if (activity !is AppCompatActivity) {
                promise.reject(ECloudPaymentsError.NO_VIEW_CONTROLLER.rawValue, EDefaultMessages.NO_VIEW_CONTROLLER_AVAILABLE.rawValue, null)
                return
            }

            // Сохраняем Promise для обработки результата
            currentPromise = PromiseWrapper(promise)

            // Создаем конфигурацию платежа
            val configuration = PaymentDataConverter.createPaymentConfiguration(publicId, paymentData)


          // Отправляем событие "willDisplay"
            eventEmitter.sendFormWillDisplay()

            // Запускаем платежную форму
            launchPaymentForm(activity, configuration)

        } catch (e: Exception) {
            promise.reject(ECloudPaymentsError.CONFIGURATION_ERROR.rawValue, EDefaultMessages.INVALID_PAYMENT_CONFIGURATION.rawValue + ": ${e.message}", e)
        }
    }

    /**
     * Запуск платежной формы через Activity Result API
     * SDK 2.1.1: Результат содержит status, transactionId, reasonCode
     */
    private fun launchPaymentForm(activity: AppCompatActivity, configuration: PaymentConfiguration) {
        try {
            // SDK 2.1.1: launcher возвращает результат с полями status, transactionId, reasonCode
            if (paymentLauncher == null) {
                paymentLauncher = CloudpaymentsSDK.getInstance().launcher(activity) { result ->
                    handlePaymentResult(
                        status = result.status,
                        transactionId = result.transactionId,
                        reasonCode = result.reasonCode ?: 0
                    )
                }
            }

            // Отправляем событие "didDisplay"
            eventEmitter.sendFormDidDisplay()

            // Запускаем платежную форму
            paymentLauncher?.launch(configuration)

        } catch (e: Exception) {
            // Отправляем событие "didHide" при ошибке
            eventEmitter.sendFormDidHide()

            currentPromise?.promise?.reject(
                EAndroidSpecific.PAYMENT_FORM_ERROR,
                EDefaultMessages.INVALID_PAYMENT_CONFIGURATION.rawValue + ": ${e.message}",
                e
            )
            currentPromise = null
        }
    }

    /**
     * Обработка результата платежа
     * SDK 2.1.1: Результат содержит status, transactionId, reasonCode
     */
    private fun handlePaymentResult(
        status: CloudpaymentsSDK.TransactionStatus?,
        transactionId: Long?,
        reasonCode: Int
    ) {
        // Отправляем событие "willHide"
        eventEmitter.sendFormWillHide()

        when (status) {
            CloudpaymentsSDK.TransactionStatus.Succeeded -> {
                handleSuccessfulPayment(transactionId ?: 0L)
            }

            CloudpaymentsSDK.TransactionStatus.Failed -> {
                handleFailedPayment(reasonCode)
            }

            null -> {
                handleCancelledPayment()
            }
        }

        // Отправляем событие "didHide"
        eventEmitter.sendFormDidHide()

        // Очищаем текущий Promise
        currentPromise = null
    }

    /**
     * Обработка успешного платежа
     */
    private fun handleSuccessfulPayment(transactionId: Long) {
        // Отправляем событие успешной транзакции
        eventEmitter.sendTransactionSuccess(
            transactionId = transactionId,
            message = EDefaultMessages.PAYMENT_COMPLETED_SUCCESSFULLY.rawValue
        )

        // Возвращаем результат в Promise
        val response = PaymentDataConverter.createSuccessResponse(
            transactionId = transactionId,
            message = EDefaultMessages.PAYMENT_COMPLETED_SUCCESSFULLY.rawValue
        )
        currentPromise?.promise?.resolve(response)
    }

    /**
     * Обработка неудачного платежа
     */
    private fun handleFailedPayment(reasonCode: Int) {
        val errorCode = PaymentDataConverter.getErrorCodeFromReasonCode(reasonCode)
        val errorMessage = PaymentDataConverter.getErrorMessage(reasonCode)

        // Отправляем событие ошибки транзакции
        eventEmitter.sendTransactionError(
            message = errorMessage,
            errorCode = errorCode
        )

        // Возвращаем ошибку в Promise
        currentPromise?.promise?.reject(
            errorCode,
            errorMessage,
            null
        )
    }

    /**
     * Обработка отмененного платежа
     */
    private fun handleCancelledPayment() {
        // Возвращаем ошибку отмены в Promise
        currentPromise?.promise?.reject(
            ECloudPaymentsError.PAYMENT_FAILED.rawValue,
            EDefaultMessages.PAYMENT_CANCELLED_BY_USER.rawValue,
            null
        )
    }

    /**
     * Очистка ресурсов
     */
    fun cleanup() {
        paymentLauncher = null
        currentPromise = null
    }
}

/**
 * Обертка для Promise
 */
private class PromiseWrapper(
    val promise: com.facebook.react.bridge.Promise
)
