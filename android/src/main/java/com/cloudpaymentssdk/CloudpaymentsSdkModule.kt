package com.cloudpaymentssdk

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.configuration.PaymentConfiguration

@ReactModule(name = EModuleNames.CLOUDPAYMENTS_SDK)
class CloudpaymentsSdkModule(reactContext: ReactApplicationContext) :
  NativeCloudpaymentsSdkSpec(reactContext), ActivityEventListener {

  private var publicId: String? = null
  private var eventEmitter = CloudPaymentsEventEmitter(reactContext)
  private var pendingPromise: Promise? = null
  private var isProcessingResult = false // Защита от повторной обработки

  companion object {
    const val NAME = EModuleNames.CLOUDPAYMENTS_SDK

    /**
     * Helper функция для получения статуса транзакции с поддержкой новых API
     * Улучшенная версия с обработкой ошибок десериализации
     */
    private fun getTransactionStatus(data: Intent?): CloudpaymentsSDK.TransactionStatus? {
      if (data == null) return null
      
      return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
          data.getSerializableExtra(
            CloudpaymentsSDK.IntentKeys.TransactionStatus.name, 
            CloudpaymentsSDK.TransactionStatus::class.java
          )
        } else {
          @Suppress("DEPRECATION")
          val serializable = data.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name)
          serializable as? CloudpaymentsSDK.TransactionStatus
        }
      } catch (e: ClassCastException) {
        // Проблема с приведением типов - логируем и возвращаем null
        null
      } catch (e: Exception) {
        // Любая другая ошибка при извлечении статуса
        null
      }
    }
  }

  init {
    reactApplicationContext.addActivityEventListener(this)
  }

  override fun getName(): String {
    return NAME
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun getPublicKey(promise: Promise) {
    try {
      // TODO: NOT IMPLEMENTED - Получение публичного ключа для шифрования
      val result = ""
      promise.resolve(result)
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun isCardNumberValid(cardNumber: String, promise: Promise) {
    try {
      // TODO: NOT IMPLEMENTED - Валидация номера банковской карты
      promise.resolve(false)
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun isExpDateValid(expDate: String, promise: Promise) {
    try {
      // TODO: NOT IMPLEMENTED - Валидация срока действия карты
      promise.resolve(false)
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun isValidCvv(cvv: String, isCvvRequired: Boolean, promise: Promise) {
    try {
      // TODO: NOT IMPLEMENTED - Валидация CVV кода карты
      promise.resolve(false)
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  @ReactMethod
  override fun makeCardCryptogramPacket(
    cardNumber: String,
    expDate: String,
    cvv: String,
    merchantPublicID: String,
    publicKey: String,
    keyVersion: Double,
    promise: Promise
  ) {
    try {
      // TODO: NOT IMPLEMENTED - Реализация расчёта криптограммы
      val result = ""
      promise.resolve(result)
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun createIntent(paymentData: ReadableMap, promise: Promise) {
    try {
      // TODO: NOT IMPLEMENTED - Создание платежного намерения
      promise.reject("NOT_IMPLEMENTED", "createIntent method not implemented yet")
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun createIntentApiPay(
    paymentData: ReadableMap,
    cardCryptogram: String,
    intentId: String,
    promise: Promise
  ) {
    try {
      // TODO: NOT IMPLEMENTED - Оплата через Intent с криптограммой
      promise.reject("NOT_IMPLEMENTED", "createIntentApiPay method not implemented yet")
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun getIntentWaitStatus(
    paymentData: ReadableMap,
    type: String,
    promise: Promise
  ) {
    try {
      // TODO: NOT IMPLEMENTED - Ожидание статуса Intent для альтернативных способов оплаты
      promise.reject("NOT_IMPLEMENTED", "getIntentWaitStatus method not implemented yet")
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun cardTypeFromCardNumber(cardNumber: String, promise: Promise) {
    try {
      // TODO: NOT IMPLEMENTED - Определение типа карты по номеру
      val result = "unknown"
      promise.resolve(result)
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }

  //TODO: NOT IMPLEMENTED
  @ReactMethod
  override fun getBankInfo(cardNumber: String, promise: Promise) {
    try {
      // TODO: NOT IMPLEMENTED - Получение информации о банке-эмитенте
      promise.reject("NOT_IMPLEMENTED", "getBankInfo method not implemented yet")
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }
  }


  @ReactMethod
  override fun initialize(publicId: String, promise: Promise) {
    try {
      this.publicId = publicId
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject(ECloudPaymentsError.CONFIGURATION_ERROR.rawValue, "Failed to initialize SDK: ${e.message}", e)
    }
  }

  @ReactMethod
  override fun presentPaymentForm(paymentData: ReadableMap, promise: Promise) {
    try {
      val currentPublicId = this.publicId
      if (currentPublicId == null) {
        promise.reject(ECloudPaymentsError.SERVICE_UNINITIALIZED.rawValue, EDefaultMessages.PAYMENT_SERVICE_NOT_INITIALIZED.rawValue)
        return
      }

      val activity = currentActivity
      if (activity !is FragmentActivity) {
        promise.reject(EAndroidSpecific.ACTIVITY_NOT_AVAILABLE, EDefaultMessages.NO_VIEW_CONTROLLER_AVAILABLE.rawValue)
        return
      }

      // Сохраняем Promise для обработки результата
      pendingPromise = promise
      
      // Сбрасываем флаг обработки результата для нового платежа
      isProcessingResult = false

      // Создаем конфигурацию платежа
      val configuration = PaymentDataConverter.createPaymentConfiguration(currentPublicId, paymentData)

      // Отправляем события
      eventEmitter.sendFormWillDisplay()
      eventEmitter.sendFormDidDisplay()

      // Запускаем платежную форму напрямую
      CloudpaymentsSDK.getInstance().start(configuration, activity, EModuleNames.PAYMENT_REQUEST_CODE)

    } catch (e: Exception) {
      promise.reject(EAndroidSpecific.PAYMENT_FORM_ERROR, "Failed to present payment form: ${e.message}", e)
    }
  }

  // ActivityEventListener методы
  override fun onActivityResult(
    activity: Activity?,
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    if (requestCode == EModuleNames.PAYMENT_REQUEST_CODE) {
      handlePaymentResult(resultCode, data)
    }
  }

  override fun onNewIntent(intent: Intent?) {
  }

  /**
   * Обработка результата платежа из onActivityResult
   * Исправленная версия с правильной логикой обработки неизвестного статуса
   */
  private fun handlePaymentResult(resultCode: Int, data: Intent?) {
    // ЗАЩИТА: Предотвращаем повторную обработку результата
    if (isProcessingResult) {
      return
    }
    
    isProcessingResult = true
    
    // Отправляем события
    eventEmitter.sendFormWillHide()

    when (resultCode) {
      Activity.RESULT_OK -> {
        // Используем улучшенную функцию для получения статуса
        val transactionStatus = getTransactionStatus(data)
        val transactionId = data?.getLongExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, 0L) ?: 0L

        when (transactionStatus) {
          CloudpaymentsSDK.TransactionStatus.Succeeded -> {
            handleSuccessfulPayment(data)
          }
          CloudpaymentsSDK.TransactionStatus.Failed -> {
            handleFailedPayment(data)
          }
          null -> {
            // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: правильная обработка неизвестного статуса
            // Если статус неизвестен, но resultCode = OK, проверяем наличие transactionId
            if (transactionId > 0L) {
              // Если есть transactionId, значит платеж прошел успешно
              handleSuccessfulPayment(data)
            } else {
              // Если нет transactionId, значит была ошибка
              handleFailedPayment(data)
            }
          }
        }
      }

      Activity.RESULT_CANCELED -> {
        // УЛУЧШЕНО: Проверяем, есть ли данные об ошибке в Intent
        val transactionStatus = getTransactionStatus(data)
        val reasonCode = data?.getIntExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, 0) ?: 0
        val transactionId = data?.getLongExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, 0L) ?: 0L
        
        when {
          // Если статус явно указывает на неудачу - это ошибка
          transactionStatus == CloudpaymentsSDK.TransactionStatus.Failed -> {
            handleFailedPayment(data)
          }
          // Если есть код ошибки, но нет ID транзакции - это ошибка
          reasonCode > 0 && transactionId <= 0L -> {
            handleFailedPayment(data)
          }
          // Если есть ID транзакции - возможно успех (редкий случай)
          transactionId > 0L -> {
            handleSuccessfulPayment(data)
          }
          // Во всех остальных случаях - отмена пользователем
          else -> {
            handleCancelledPayment()
          }
        }
      }

      else -> {
        // Любой другой код результата считаем ошибкой, а не отменой
        handleFailedPayment(data)
      }
    }

    eventEmitter.sendFormDidHide()

    // Очищаем Promise и сбрасываем флаг
    pendingPromise = null
    isProcessingResult = false
  }

  /**
   * Обработка успешного платежа
   */
  private fun handleSuccessfulPayment(data: Intent?) {
    try {
      // Извлекаем данные транзакции из Intent используя ключи CloudPayments SDK
      val transactionId = data?.getLongExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, EDefaultValues.DEFAULT_TRANSACTION_ID) ?: EDefaultValues.DEFAULT_TRANSACTION_ID
      val transactionStatus = data?.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name) as? CloudpaymentsSDK.TransactionStatus
      val reasonCode = data?.getIntExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, 0) ?: 0

      // Отправляем событие успешной транзакции
      eventEmitter.sendTransactionSuccess(
        transactionId = transactionId,
        message = EDefaultMessages.PAYMENT_COMPLETED_SUCCESSFULLY.rawValue
      )

      // Создаем результат для Promise - используем тот же формат что в iOS
      val result = Arguments.createMap().apply {
        putBoolean(EResponseKeys.SUCCESS.rawValue, true)
        putDouble(EResponseKeys.TRANSACTION_ID.rawValue, transactionId.toDouble())
        putString(EResponseKeys.MESSAGE.rawValue, EDefaultMessages.PAYMENT_COMPLETED_SUCCESSFULLY.rawValue)
      }

      pendingPromise?.resolve(result)
    } catch (e: Exception) {
      pendingPromise?.reject(EAndroidSpecific.SUCCESS_PROCESSING_ERROR, e.message, e)
    }
  }

  /**
   * Обработка неудачного платежа
   */
  private fun handleFailedPayment(data: Intent?) {
    try {
      // Извлекаем данные об ошибке из Intent
      val transactionId = data?.getLongExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, EDefaultValues.DEFAULT_TRANSACTION_ID) ?: EDefaultValues.DEFAULT_TRANSACTION_ID
      val transactionStatus = data?.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name) as? CloudpaymentsSDK.TransactionStatus
      val reasonCode = data?.getIntExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, 0) ?: 0

      // Получаем код ошибки и сообщение на основе reasonCode
      val errorCode = PaymentDataConverter.getErrorCodeFromReasonCode(reasonCode)
      val errorMessage = PaymentDataConverter.getErrorMessage(reasonCode)

      // Отправляем событие ошибки транзакции
      eventEmitter.sendTransactionError(
        message = errorMessage,
        errorCode = errorCode
      )

      // Создаем результат с ошибкой для Promise - используем тот же формат что в iOS
      val result = Arguments.createMap().apply {
        putBoolean(EResponseKeys.SUCCESS.rawValue, false)
        putString(EResponseKeys.ERROR_CODE.rawValue, errorCode)
        putString(EResponseKeys.MESSAGE.rawValue, errorMessage)
        if (transactionId != EDefaultValues.DEFAULT_TRANSACTION_ID) {
          putDouble(EResponseKeys.TRANSACTION_ID.rawValue, transactionId.toDouble())
        }
      }

      pendingPromise?.resolve(result)
    } catch (e: Exception) {
      pendingPromise?.reject(EAndroidSpecific.FAILED_PROCESSING_ERROR, e.message, e)
    }
  }

  /**
   * Обработка отменённого платежа
   */
  private fun handleCancelledPayment() {
    // Отправляем событие отмены платежа (НЕ ошибки!)
    eventEmitter.sendTransactionCancelled(EDefaultMessages.PAYMENT_CANCELLED_BY_USER.rawValue)

    // Создаем результат отмены для Promise - используем тот же формат что в iOS
    val result = Arguments.createMap().apply {
      putBoolean(EResponseKeys.SUCCESS.rawValue, false)
      putString(EResponseKeys.STATUS.rawValue, EPaymentResultValues.CANCELLED.rawValue)
      putString(EResponseKeys.MESSAGE.rawValue, EDefaultMessages.PAYMENT_CANCELLED_BY_USER.rawValue)
      putString(EResponseKeys.ERROR_CODE.rawValue, ECloudPaymentsError.PAYMENT_FAILED.rawValue)
    }

    pendingPromise?.resolve(result)
  }

  /**
   * Освобождение ресурсов при уничтожении модуля
   */
  override fun onCatalystInstanceDestroy() {
    pendingPromise = null
  }
}
