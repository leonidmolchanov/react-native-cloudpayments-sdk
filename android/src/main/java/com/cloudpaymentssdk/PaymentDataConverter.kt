package com.cloudpaymentssdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.configuration.PaymentData
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.models.Transaction
import ru.cloudpayments.sdk.api.models.PaymentDataPayer

/**
 * Утилиты для конвертации данных между React Native и CloudPayments Android SDK
 *
 * @description Класс для преобразования данных из JavaScript в Android объекты и обратно.
 * Обеспечивает совместимость типов данных между платформами.
 *
 * @author Leonid Molchanov
 * @since 1.0.0
 */
object PaymentDataConverter {
    
    /**
     * Создание PaymentDataPayer из React Native данных
     *
     * @param payerMap Данные плательщика из JavaScript
     * @return PaymentDataPayer для Android SDK
     */
    private fun createPaymentDataPayer(payerMap: ReadableMap?): PaymentDataPayer? {
        if (payerMap == null) return null
        
        return PaymentDataPayer(
            firstName = payerMap.getString(EPayerDataKeys.FIRST_NAME.rawValue),
            lastName = payerMap.getString(EPayerDataKeys.LAST_NAME.rawValue),
            middleName = payerMap.getString(EPayerDataKeys.MIDDLE_NAME.rawValue),
            birthDay = payerMap.getString(EPayerDataKeys.BIRTH.rawValue),
            address = payerMap.getString(EPayerDataKeys.ADDRESS.rawValue),
            street = payerMap.getString(EPayerDataKeys.STREET.rawValue),
            city = payerMap.getString(EPayerDataKeys.CITY.rawValue),
            country = payerMap.getString(EPayerDataKeys.COUNTRY.rawValue),
            phone = payerMap.getString(EPayerDataKeys.PHONE.rawValue),
            postcode = payerMap.getString(EPayerDataKeys.POSTCODE.rawValue)
        )
    }
    
    /**
     * Создание PaymentConfiguration из React Native данных
     *
     * @param publicId Публичный ID мерчанта
     * @param paymentDataMap Данные платежа из JavaScript
     * @return PaymentConfiguration для Android SDK
     */
    fun createPaymentConfiguration(
        publicId: String,
        paymentDataMap: ReadableMap
    ): PaymentConfiguration {
        
        // Обработка jsonData - если не передано или пустое, используем пустой объект (не null!)
        val jsonDataString = if (paymentDataMap.hasKey(EPaymentConfigKeys.JSON_DATA.rawValue)) {
            val jsonData = paymentDataMap.getString(EPaymentConfigKeys.JSON_DATA.rawValue)
            if (!jsonData.isNullOrBlank()) {
                jsonData
            } else {
                "{}"
            }
        } else {
            "{}"
        }
        
        // Создаем объект payer если данные переданы
        val payer = if (paymentDataMap.hasKey(EPayerDataKeys.PAYER.rawValue)) {
            val payerData = createPaymentDataPayer(paymentDataMap.getMap(EPayerDataKeys.PAYER.rawValue))
            payerData
        } else {
            null
        }
        
        // Создаем CardIO сканер если включен или передана конфигурация
        val cardScanner = if (paymentDataMap.hasKey(ECardIOConstants.ENABLE_CARD_SCANNER) && 
                             paymentDataMap.getBoolean(ECardIOConstants.ENABLE_CARD_SCANNER)) {
            val cardScannerConfig = paymentDataMap.getMap(ECardIOConstants.CARD_SCANNER_CONFIG)
            CardIOScanner.fromJSConfig(cardScannerConfig)
        } else if (paymentDataMap.hasKey(ECardIOConstants.CARD_SCANNER_CONFIG)) {
            // Если передана конфигурация сканера, но enableCardScanner не указан - включаем автоматически
            val cardScannerConfig = paymentDataMap.getMap(ECardIOConstants.CARD_SCANNER_CONFIG)
            CardIOScanner.fromJSConfig(cardScannerConfig)
        } else {
            // По умолчанию включаем сканер с базовыми настройками
            CardIOScanner()
        }
        
        val paymentData = PaymentData(
            amount = paymentDataMap.getString(EPaymentConfigKeys.AMOUNT.rawValue) ?: "0",
            currency = paymentDataMap.getString(EPaymentConfigKeys.CURRENCY.rawValue) ?: ECurrency.RUB,
            description = paymentDataMap.getString(EPaymentConfigKeys.DESCRIPTION.rawValue),
            accountId = paymentDataMap.getString(EPaymentConfigKeys.ACCOUNT_ID.rawValue),
            email = paymentDataMap.getString(EPaymentConfigKeys.EMAIL.rawValue),
            externalId = paymentDataMap.getString(EPaymentResultValues.EXTERNAL_ID.rawValue),
            payer = payer,
            jsonData = jsonDataString // Всегда передаем валидный JSON
        )
        
        return PaymentConfiguration(
            publicId = publicId,
            paymentData = paymentData,
            scanner = cardScanner,
            requireEmail = paymentDataMap.hasKey(EPaymentConfigKeys.REQUIRE_EMAIL.rawValue) && 
                          paymentDataMap.getBoolean(EPaymentConfigKeys.REQUIRE_EMAIL.rawValue),
            useDualMessagePayment = paymentDataMap.hasKey(EPaymentConfigKeys.USE_DUAL_MESSAGE_PAYMENT.rawValue) && 
                                   paymentDataMap.getBoolean(EPaymentConfigKeys.USE_DUAL_MESSAGE_PAYMENT.rawValue)
        )
    }
    
    /**
     * Конвертация Transaction в WritableMap для JavaScript
     *
     * @param transaction Результат транзакции от Android SDK
     * @return WritableMap для отправки в JavaScript
     */
    fun transactionToWritableMap(transaction: Transaction): WritableMap {
        return Arguments.createMap().apply {
            putBoolean(EResponseKeys.SUCCESS.rawValue, transaction.status == CloudpaymentsSDK.TransactionStatus.Succeeded)
            
            transaction.transactionId?.let { 
                putDouble(EResponseKeys.TRANSACTION_ID.rawValue, it.toDouble()) 
            }
            
            transaction.status?.let { status ->
                putString(EResponseKeys.STATUS.rawValue, when (status) {
                    CloudpaymentsSDK.TransactionStatus.Succeeded -> EPaymentResultValues.SUCCEEDED.rawValue
                    CloudpaymentsSDK.TransactionStatus.Failed -> EDefaultMessages.PAYMENT_FAILED.rawValue
                })
            }
            
            transaction.reasonCode?.let { 
                putInt(EPaymentResultValues.REASON_CODE.rawValue, it) 
            }
        }
    }
    
    /**
     * Создание ответа об ошибке для JavaScript
     *
     * @param errorCode Код ошибки
     * @param message Сообщение об ошибке
     * @param details Дополнительные детали (опционально)
     * @return WritableMap с информацией об ошибке
     */
    fun createErrorResponse(
        errorCode: String,
        message: String,
        details: String? = null
    ): WritableMap {
        return Arguments.createMap().apply {
            putBoolean(EResponseKeys.SUCCESS.rawValue, false)
            putString(EResponseKeys.ERROR_CODE.rawValue, errorCode)
            putString(EResponseKeys.MESSAGE.rawValue, message)
            details?.let { putString(EPaymentResultValues.DETAILS.rawValue, it) }
        }
    }
    
    /**
     * Создание успешного ответа для JavaScript
     *
     * @param transactionId ID транзакции
     * @param message Сообщение (опционально)
     * @return WritableMap с результатом успешной операции
     */
    fun createSuccessResponse(
        transactionId: Long? = null,
        message: String? = null
    ): WritableMap {
        return Arguments.createMap().apply {
            putBoolean(EResponseKeys.SUCCESS.rawValue, true)
            transactionId?.let { putDouble(EResponseKeys.TRANSACTION_ID.rawValue, it.toDouble()) }
            message?.let { putString(EResponseKeys.MESSAGE.rawValue, it) }
        }
    }
    
    /**
     * Получение способа оплаты из строки
     *
     * @param paymentMethod Строковое представление способа оплаты
     * @return CloudpaymentsSDK.SDKRunMode
     */
    fun getSDKRunMode(paymentMethod: String?): CloudpaymentsSDK.SDKRunMode {
        return when (paymentMethod?.lowercase()) {
            EPaymentMethodType.TPAY.rawValue, EPaymentMethodType.TINKOFFPAY.rawValue -> CloudpaymentsSDK.SDKRunMode.TPay
            EPaymentMethodType.SBP.rawValue -> CloudpaymentsSDK.SDKRunMode.SBP
            EPaymentMethodType.SBERPAY.rawValue -> CloudpaymentsSDK.SDKRunMode.SberPay
            else -> CloudpaymentsSDK.SDKRunMode.SelectPaymentMethod
        }
    }
    
    /**
     * Конвертация кода ошибки CloudPayments в строку
     *
     * @param reasonCode Числовой код ошибки
     * @return Строковое представление ошибки
     */
    fun getErrorCodeFromReasonCode(reasonCode: Int?): String {
        return when (reasonCode) {
            5001 -> ECloudPaymentsError.CONFIGURATION_ERROR.rawValue
            5006 -> ECloudPaymentsError.MERCHANT_CONFIG_ERROR.rawValue
            5012 -> ECloudPaymentsError.PAYMENT_FAILED.rawValue
            5013 -> ECloudPaymentsError.INVALID_PAYMENT_TYPE.rawValue
            5030 -> ECloudPaymentsError.INTENT_WAIT_STATUS_ERROR.rawValue
            else -> EDefaultMessages.UNKNOWN_ERROR.rawValue
        }
    }
    
    /**
     * Получение человекочитаемого сообщения об ошибке
     *
     * @param reasonCode Числовой код ошибки
     * @return Описание ошибки на русском языке
     */
    fun getErrorMessage(reasonCode: Int?): String {
        return when (reasonCode) {
            5001 -> EDefaultMessages.INVALID_PAYMENT_CONFIGURATION.rawValue
            5006 -> "Неверный Public ID или мерчант заблокирован"
            5012 -> EDefaultMessages.PAYMENT_FAILED.rawValue
            5013 -> "Неподдерживаемый тип платежа"
            5030 -> "Ошибка ожидания статуса платежа"
            else -> EDefaultMessages.UNKNOWN_ERROR.rawValue
        }
    }
    
    /**
     * Создает результат для успешного платежа
     */
    fun createSuccessResult(transactionId: Long, cardMask: String, amount: Double): WritableMap {
        val result = Arguments.createMap()
        result.putString(EResponseKeys.STATUS.rawValue, EPaymentResultValues.SUCCEEDED.rawValue)
        result.putDouble(EResponseKeys.TRANSACTION_ID.rawValue, transactionId.toDouble())
        result.putString(EPaymentResultValues.CARD_MASK.rawValue, cardMask)
        result.putDouble(EPaymentResultValues.AMOUNT.rawValue, amount)
        result.putString(EPaymentConfigKeys.CURRENCY.rawValue, ECurrency.RUB)
        return result
    }
} 