package com.cloudpaymentssdk

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
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

  private var hasActivePaymentAttempt = false
  private var shouldSendDidDisplay = false
  private var lifecycleCallback: Application.ActivityLifecycleCallbacks? = null

  companion object {
    const val NAME = EModuleNames.CLOUDPAYMENTS_SDK

    /**
     * Helper функция для получения статуса транзакции с поддержкой новых API
     * Улучшенная версия с обработкой ошибок десериализации
     */
    fun getTransactionStatus(data: Intent?): CloudpaymentsSDK.TransactionStatus? {
      if (data == null) {
        return null
      }

      return try {
        val status = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
          data.getSerializableExtra(
            CloudpaymentsSDK.IntentKeys.TransactionStatus.name,
            CloudpaymentsSDK.TransactionStatus::class.java
          )
        } else {
          @Suppress("DEPRECATION")
          val serializable = data.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name)
          serializable as? CloudpaymentsSDK.TransactionStatus
        }
        status
      } catch (e: ClassCastException) {
        null
      } catch (e: Exception) {
        null
      }
    }
  }

  init {
    reactApplicationContext.addActivityEventListener(this)
    registerLifecycleCallback()
  }
  
  /**
   * Регистрация lifecycle callback для отслеживания, когда PaymentActivity действительно отобразилась
   */
  private fun registerLifecycleCallback() {
    val application = reactApplicationContext.currentActivity?.application as? Application
    if (application != null) {
      lifecycleCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
          val activityClassName = activity.javaClass.name
          if (activityClassName == "ru.cloudpayments.sdk.ui.PaymentActivity" && shouldSendDidDisplay) {
            shouldSendDidDisplay = false
            eventEmitter.sendFormDidDisplay()
          }
        }
        
        override fun onActivityCreated(activity: Activity, savedInstanceState: android.os.Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: android.os.Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
      }
      application.registerActivityLifecycleCallbacks(lifecycleCallback)
    } else {
    }
  }
  
  /**
   * Отмена регистрации lifecycle callback
   */
  private fun unregisterLifecycleCallback() {
    val application = reactApplicationContext.currentActivity?.application as? Application
    lifecycleCallback?.let { callback ->
      application?.unregisterActivityLifecycleCallbacks(callback)
      lifecycleCallback = null
    }
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

      // Сбрасываем флаги для нового платежа
      isProcessingResult = false
      hasActivePaymentAttempt = true

      // Создаем конфигурацию платежа
      val configuration = PaymentDataConverter.createPaymentConfiguration(currentPublicId, paymentData)

      // Отправляем событие willDisplay
      eventEmitter.sendFormWillDisplay()

      // Устанавливаем флаг, что нужно отправить didDisplay когда Activity отобразится
      shouldSendDidDisplay = true

      // Запускаем платежную форму напрямую
      CloudpaymentsSDK.getInstance().start(configuration, activity, EModuleNames.PAYMENT_REQUEST_CODE)

    } catch (e: Exception) {
      promise.reject(EAndroidSpecific.PAYMENT_FORM_ERROR, "Failed to present payment form: ${e.message}", e)
    }
  }

  // ActivityEventListener методы
  override fun onActivityResult(
    activity: Activity,
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    if (requestCode == EModuleNames.PAYMENT_REQUEST_CODE) {
      handlePaymentResult(resultCode, data)
    }
  }

  override fun onNewIntent(intent: Intent) {
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

    val transactionStatus = getTransactionStatus(data)
    
    // Проверяем все возможные ключи для transactionId
    val transactionIdKey = CloudpaymentsSDK.IntentKeys.TransactionId.name
    val transactionId = try {
      data?.getLongExtra(transactionIdKey, 0L) ?: 0L
    } catch (e: Exception) {
      try {
        // Пробуем как Int
        val intValue = data?.getIntExtra(transactionIdKey, 0) ?: 0
        intValue.toLong()
      } catch (e2: Exception) {
        0L
      }
    }

    // reasonCode может быть как String (старый SDK) так и Int (новый SDK)
    val reasonCode: Int = try {
      // Сначала пробуем получить как Int (новый SDK)
      val intValue = data?.getIntExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, 0) ?: 0
      intValue
    } catch (e: Exception) {
      // Если не удалось получить как Int, пробуем как String (старый SDK)
      try {
        val reasonCodeString = data?.getStringExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name)
        val stringValue = reasonCodeString?.toIntOrNull() ?: 0
        stringValue
      } catch (e2: Exception) {
        0
      }
    }

    // Проверяем, есть ли URL с информацией об ошибке (например, из 3D Secure)
    val dataString = data?.dataString

    if (dataString != null && dataString.contains("threeds/fail")) {
      // Извлекаем информацию об ошибке из URL
      try {
        val uri = android.net.Uri.parse(dataString)
        val success = uri.getQueryParameter("Success")
        val reasonCodeFromUrl = uri.getQueryParameter("ReasonCode")
        val transactionIdFromUrl = uri.getQueryParameter("TransactionId")

        if (success == "False" && reasonCodeFromUrl != null) {
          // Создаем фиктивный Intent с данными об ошибке
          val errorIntent = android.content.Intent().apply {
            putExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name, CloudpaymentsSDK.TransactionStatus.Failed)
            putExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, reasonCodeFromUrl.toIntOrNull() ?: 0)
            transactionIdFromUrl?.toLongOrNull()?.let {
              putExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, it)
            }
          }

          eventEmitter.sendFormWillHide()
          handleFailedPayment(errorIntent)
          eventEmitter.sendFormDidHide()

          // Промис уже очищен в handleFailedPayment

          // Сбрасываем флаги
          isProcessingResult = false
          hasActivePaymentAttempt = false

          return
        }
      } catch (e: Exception) {
        // Игнорируем ошибки парсинга URL
      }
    }

    // Отправляем событие willHide перед обработкой результата
    eventEmitter.sendFormWillHide()

    // В старом SDK все методы вызывают setResult(RESULT_OK, ...), даже для ошибок
    // Статус определяется по TransactionStatus в Intent, а не по resultCode
    // Поэтому сначала проверяем transactionStatus, а не resultCode
    when (transactionStatus) {
      CloudpaymentsSDK.TransactionStatus.Succeeded -> {
        // Успешный платеж - всегда обрабатываем как успех
        handleSuccessfulPayment(data)
      }
      
      CloudpaymentsSDK.TransactionStatus.Failed -> {
        // Неудачный платеж - всегда обрабатываем как ошибку
        handleFailedPayment(data)
      }
      
      null -> {
        // Если статус неизвестен, определяем по resultCode и другим признакам
        when (resultCode) {
          Activity.RESULT_OK -> {
            // Если resultCode = OK, но статус неизвестен, проверяем наличие transactionId
            if (transactionId > 0L) {
              // Если есть transactionId, значит платеж прошел успешно
              handleSuccessfulPayment(data)
            } else if (reasonCode > 0) {
              // Если есть reasonCode, значит была ошибка
              handleFailedPayment(data)
            } else {
              // Если нет ни transactionId, ни reasonCode, считаем отменой
              handleCancelledPayment()
            }
          }
          
          Activity.RESULT_CANCELED -> {
            // Если resultCode = CANCELED, проверяем дополнительные признаки
            when {
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
      }
    }

    // Отправляем событие didHide после обработки результата
    // Промис уже разрешен/реджекчен и очищен в handleSuccessfulPayment/handleFailedPayment/handleCancelledPayment
    eventEmitter.sendFormDidHide()

    // Сбрасываем флаги
    isProcessingResult = false
    hasActivePaymentAttempt = false
  }

  /**
   * Обработка успешного платежа
   */
  private fun handleSuccessfulPayment(data: Intent?) {
    try {
      // Извлекаем данные транзакции из Intent используя ключи CloudPayments SDK
      val transactionId = data?.getLongExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, EDefaultValues.DEFAULT_TRANSACTION_ID) ?: EDefaultValues.DEFAULT_TRANSACTION_ID

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

      // Разрешаем промис на главном потоке
      val promise = pendingPromise
      Handler(Looper.getMainLooper()).post {
        try {
          promise?.resolve(result)
          pendingPromise = null
        } catch (e: Exception) {
          // Игнорируем ошибки разрешения промиса
        }
      }
    } catch (e: Exception) {
      val promise = pendingPromise
      Handler(Looper.getMainLooper()).post {
        promise?.reject(EAndroidSpecific.SUCCESS_PROCESSING_ERROR, e.message, e)
        pendingPromise = null
      }
    }
  }

  /**
   * Обработка неудачного платежа
   */
  private fun handleFailedPayment(data: Intent?) {
    try {
      // Извлекаем данные об ошибке из Intent
      val transactionId = data?.getLongExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, EDefaultValues.DEFAULT_TRANSACTION_ID) ?: EDefaultValues.DEFAULT_TRANSACTION_ID

      // reasonCode может быть как String (старый SDK) так и Int (новый SDK)
      val reasonCode: Int = try {
        val intValue = data?.getIntExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, 0) ?: 0
        intValue
      } catch (e: Exception) {
        try {
          val reasonCodeString = data?.getStringExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name)
          val stringValue = reasonCodeString?.toIntOrNull() ?: 0
          stringValue
        } catch (e2: Exception) {
          0
        }
      }

      // Получаем код ошибки и сообщение на основе reasonCode
      val errorCode = PaymentDataConverter.getErrorCodeFromReasonCode(reasonCode)
      val errorMessage = PaymentDataConverter.getErrorMessage(reasonCode)

      // Отправляем событие ошибки транзакции
      eventEmitter.sendTransactionError(
        message = errorMessage,
        errorCode = errorCode
      )

      // Реджектим промис на главном потоке
      val promise = pendingPromise
      Handler(Looper.getMainLooper()).post {
        try {
          promise?.reject(
            errorCode,
            errorMessage,
            null
          )
          pendingPromise = null
        } catch (e: Exception) {
        }
      }
    } catch (e: Exception) {
      // Только в случае исключения reject Promise с ошибкой
      val promise = pendingPromise
      Handler(Looper.getMainLooper()).post {
        promise?.reject(EAndroidSpecific.FAILED_PROCESSING_ERROR, e.message, e)
        pendingPromise = null
      }
    }
  }

  /**
   * Обработка отменённого платежа
   */
  private fun handleCancelledPayment() {
    // Отправляем событие отмены
    eventEmitter.sendTransactionCancelled(EDefaultMessages.PAYMENT_CANCELLED_BY_USER.rawValue)

    // Реджектим промис на главном потоке при отмене
    val cancelMessage = EDefaultMessages.PAYMENT_CANCELLED_BY_USER.rawValue
    val promise = pendingPromise
    Handler(Looper.getMainLooper()).post {
      try {
        promise?.reject(
          ECloudPaymentsError.PAYMENT_FAILED.rawValue,
          cancelMessage,
          null
        )
        pendingPromise = null
      } catch (e: Exception) {
      }
    }
    hasActivePaymentAttempt = false
  }

  /**
   * Освобождение ресурсов при уничтожении модуля
   */
  override fun onCatalystInstanceDestroy() {
    unregisterLifecycleCallback()
    pendingPromise = null
  }
}
