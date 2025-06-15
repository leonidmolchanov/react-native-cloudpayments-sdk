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

  companion object {
    const val NAME = EModuleNames.CLOUDPAYMENTS_SDK
    
    /**
     * Helper функция для получения статуса транзакции с поддержкой новых API
     */
    private fun getTransactionStatus(data: Intent?): CloudpaymentsSDK.TransactionStatus? {
      return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        data?.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name, CloudpaymentsSDK.TransactionStatus::class.java)
      } else {
        @Suppress("DEPRECATION")
        data?.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name) as? CloudpaymentsSDK.TransactionStatus
      }
    }
  }

  init {
    reactApplicationContext.addActivityEventListener(this)
  }

  override fun getName(): String {
    return NAME
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
   */
  private fun handlePaymentResult(resultCode: Int, data: Intent?) {
    // Отправляем события
    eventEmitter.sendFormWillHide()
    
    when (resultCode) {
      Activity.RESULT_OK -> {
        // Проверяем статус транзакции из Intent
        val transactionStatus = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
          data?.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name, CloudpaymentsSDK.TransactionStatus::class.java)
        } else {
          @Suppress("DEPRECATION")
          data?.getSerializableExtra(CloudpaymentsSDK.IntentKeys.TransactionStatus.name) as? CloudpaymentsSDK.TransactionStatus
        }
        
        when (transactionStatus) {
          CloudpaymentsSDK.TransactionStatus.Succeeded -> {
            handleSuccessfulPayment(data)
          }
          CloudpaymentsSDK.TransactionStatus.Failed -> {
            handleFailedPayment(data)
          }
          else -> {
            handleCancelledPayment()
          }
        }
      }
      
      Activity.RESULT_CANCELED -> {
        handleCancelledPayment()
      }
      
      else -> {
        handleCancelledPayment()
      }
    }
    
    eventEmitter.sendFormDidHide()
    
    // Очищаем Promise
    pendingPromise = null
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
    // Отправляем событие ошибки транзакции (отмена)
    eventEmitter.sendTransactionError(EDefaultMessages.PAYMENT_CANCELLED_BY_USER.rawValue, ECloudPaymentsError.PAYMENT_FAILED.rawValue)
    
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
