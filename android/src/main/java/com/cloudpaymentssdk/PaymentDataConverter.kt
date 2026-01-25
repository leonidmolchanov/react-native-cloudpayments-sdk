package com.cloudpaymentssdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.configuration.PaymentData
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.configuration.EmailBehavior
import ru.cloudpayments.sdk.api.models.PaymentDataPayer
import org.json.JSONObject
import org.json.JSONArray
import ru.cloudpayments.sdk.api.models.intent.CPRecurrent
import android.util.Log

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

  private fun readableMapToHashMap(map: ReadableMap?): HashMap<String, Any?> {
    val result = HashMap<String, Any?>()
    if (map == null) return result

    val it = map.keySetIterator()
    while (it.hasNextKey()) {
      val key = it.nextKey()
      when (map.getType(key)) {
        ReadableType.Null -> result[key] = null
        ReadableType.Boolean -> result[key] = map.getBoolean(key)
        ReadableType.Number -> {
          val d = map.getDouble(key)
          result[key] = if (d == d.toLong().toDouble()) d.toLong() else d
        }
        ReadableType.String -> result[key] = map.getString(key)
        ReadableType.Map -> result[key] = readableMapToHashMap(map.getMap(key))
        ReadableType.Array -> result[key] = readableArrayToArrayList(map.getArray(key))
      }
    }
    return result
  }

  private fun readableArrayToArrayList(arr: ReadableArray?): ArrayList<Any?> {
    val result = ArrayList<Any?>()
    if (arr == null) return result

    for (i in 0 until arr.size()) {
      when (arr.getType(i)) {
        ReadableType.Null -> result.add(null)
        ReadableType.Boolean -> result.add(arr.getBoolean(i))
        ReadableType.Number -> {
          val d = arr.getDouble(i)
          result.add(if (d == d.toLong().toDouble()) d.toLong() else d)
        }
        ReadableType.String -> result.add(arr.getString(i))
        ReadableType.Map -> result.add(readableMapToHashMap(arr.getMap(i)))
        ReadableType.Array -> result.add(readableArrayToArrayList(arr.getArray(i)))
      }
    }
    return result
  }

  private fun normalizeReceiptMap(receipt: HashMap<String, Any?>): HashMap<String, Any?> {
    // items -> Items
    if (receipt.containsKey("items") && !receipt.containsKey("Items")) {
      receipt["Items"] = receipt["items"]
      receipt.remove("items")
    }
    // isBso -> IsBso (очень частая ошибка)
    if (receipt.containsKey("isBso") && !receipt.containsKey("IsBso")) {
      receipt["IsBso"] = receipt["isBso"]
      receipt.remove("isBso")
    }
    return receipt
  }

  private fun normalizeRecurrentMap(recurrent: HashMap<String, Any?>): HashMap<String, Any?> {
    // customerReceipt -> CustomerReceipt (+ items -> Items внутри)
    val cr = recurrent["customerReceipt"]
    if (cr is HashMap<*, *>) {
      @Suppress("UNCHECKED_CAST")
      val customerReceipt = cr as HashMap<String, Any?>
      normalizeReceiptMap(customerReceipt)

      if (!recurrent.containsKey("CustomerReceipt")) {
        recurrent["CustomerReceipt"] = customerReceipt
      }
      recurrent.remove("customerReceipt")
    }
    return recurrent
  }



  /**
     * Конвертация ReadableMap в JSON строку
     *
     * @param readableMap Карта данных из React Native
     * @return JSON строка
     */
    private fun readableMapToJson(readableMap: ReadableMap?): String {
        if (readableMap == null) return "{}"

        val jsonObject = JSONObject()
        val iterator = readableMap.keySetIterator()

        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()

            try {
                when (readableMap.getType(key)) {
                    ReadableType.Null -> jsonObject.put(key, JSONObject.NULL)
                    ReadableType.Boolean -> jsonObject.put(key, readableMap.getBoolean(key))
                    ReadableType.Number -> {
                        // React Native может передавать как Double так и Int
                        val doubleValue = readableMap.getDouble(key)
                        if (doubleValue == doubleValue.toLong().toDouble()) {
                            jsonObject.put(key, doubleValue.toLong())
                        } else {
                            jsonObject.put(key, doubleValue)
                        }
                    }
                    ReadableType.String -> jsonObject.put(key, readableMap.getString(key))
                    ReadableType.Map -> jsonObject.put(key, JSONObject(readableMapToJson(readableMap.getMap(key))))
                    ReadableType.Array -> jsonObject.put(key, readableArrayToJson(readableMap.getArray(key)))
                }
            } catch (e: Exception) {
                // Если не удается конвертировать значение, пропускаем его
                continue
            }
        }

        return jsonObject.toString()
    }

    /**
     * Конвертация ReadableArray в JSONArray
     *
     * @param readableArray Массив данных из React Native
     * @return JSONArray
     */
    private fun readableArrayToJson(readableArray: ReadableArray?): JSONArray {
        val jsonArray = JSONArray()
        if (readableArray == null) return jsonArray

        for (i in 0 until readableArray.size()) {
            try {
                when (readableArray.getType(i)) {
                    ReadableType.Null -> jsonArray.put(JSONObject.NULL)
                    ReadableType.Boolean -> jsonArray.put(readableArray.getBoolean(i))
                    ReadableType.Number -> {
                        val doubleValue = readableArray.getDouble(i)
                        if (doubleValue == doubleValue.toLong().toDouble()) {
                            jsonArray.put(doubleValue.toLong())
                        } else {
                            jsonArray.put(doubleValue)
                        }
                    }
                    ReadableType.String -> jsonArray.put(readableArray.getString(i))
                    ReadableType.Map -> jsonArray.put(JSONObject(readableMapToJson(readableArray.getMap(i))))
                    ReadableType.Array -> jsonArray.put(readableArrayToJson(readableArray.getArray(i)))
                }
            } catch (e: Exception) {
                // Если не удается конвертировать значение, пропускаем его
                continue
            }
        }

        return jsonArray
    }


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
     * Обновлено для SDK 2.1.1
     *
     * @param publicId Публичный ID мерчанта
     * @param paymentDataMap Данные платежа из JavaScript
     * @return PaymentConfiguration для Android SDK
     */
    fun createPaymentConfiguration(
        publicId: String,
        paymentDataMap: ReadableMap
    ): PaymentConfiguration {

        // Обработка jsonData - конвертируем ReadableMap в JSON строку
        val jsonDataString = try {
            val jsonDataObject = if (paymentDataMap.hasKey(EPaymentConfigKeys.JSON_DATA.rawValue)) {
                val jsonDataMap = paymentDataMap.getMap(EPaymentConfigKeys.JSON_DATA.rawValue)
                JSONObject(readableMapToJson(jsonDataMap))
            } else {
                JSONObject()
            }

            // SDK 2.1.1: Receipt передается как Map<String, Any>
            if (paymentDataMap.hasKey(EPaymentConfigKeys.RECEIPT.rawValue)) {

              val receiptMap = paymentDataMap.getMap(EPaymentConfigKeys.RECEIPT.rawValue)
                val receiptJson = JSONObject(readableMapToJson(receiptMap))

              // Приводим ключи к ожидаемому формату API: items -> Items
                if (receiptJson.has("items") && !receiptJson.has("Items")) {
                    receiptJson.put("Items", receiptJson.getJSONArray("items"))
                    receiptJson.remove("items")
                }

                val cloudPaymentsJson = if (jsonDataObject.has(EPaymentConfigKeys.CLOUDPAYMENTS.rawValue)) {
                    jsonDataObject.getJSONObject(EPaymentConfigKeys.CLOUDPAYMENTS.rawValue)
                } else {
                    val newCloudPayments = JSONObject()
                    jsonDataObject.put(EPaymentConfigKeys.CLOUDPAYMENTS.rawValue, newCloudPayments)
                    newCloudPayments
                }


                cloudPaymentsJson.put(EPaymentConfigKeys.CUSTOMER_RECEIPT.rawValue, receiptJson)
            }

            if (paymentDataMap.hasKey(EPaymentConfigKeys.RECURRENT.rawValue)) {
                val recurrentMap = paymentDataMap.getMap(EPaymentConfigKeys.RECURRENT.rawValue)
                val recurrentJson = JSONObject(readableMapToJson(recurrentMap))
                // customerReceipt -> CustomerReceipt и внутри него items -> Items
                if (recurrentJson.has("customerReceipt")) {
                    val cr = recurrentJson.getJSONObject("customerReceipt")
                    if (cr.has("items") && !cr.has("Items")) {
                        cr.put("Items", cr.getJSONArray("items"))
                        cr.remove("items")
                    }
                    recurrentJson.put("CustomerReceipt", cr)
                    recurrentJson.remove("customerReceipt")
                }

                val cloudPaymentsJson = if (jsonDataObject.has(EPaymentConfigKeys.CLOUDPAYMENTS.rawValue)) {
                    jsonDataObject.getJSONObject(EPaymentConfigKeys.CLOUDPAYMENTS.rawValue)
                } else {
                    JSONObject().also { jsonDataObject.put(EPaymentConfigKeys.CLOUDPAYMENTS.rawValue, it) }
                }

                cloudPaymentsJson.put(EPaymentConfigKeys.UPPER_RECURRENT.rawValue, recurrentJson)
            }

            jsonDataObject.toString()
        } catch (e: Exception) {
            "{}"
        }

        // Создаем объект payer если данные переданы
        val payer = if (paymentDataMap.hasKey(EPayerDataKeys.PAYER.rawValue)) {
            createPaymentDataPayer(paymentDataMap.getMap(EPayerDataKeys.PAYER.rawValue))
        } else {
            null
        }

        // SDK 2.1.1: Обработка emailBehavior (с fallback на старый requireEmail)
        val emailBehavior = when {
            paymentDataMap.hasKey(EPaymentConfigKeys.EMAIL_BEHAVIOR.rawValue) -> {
                when (paymentDataMap.getString(EPaymentConfigKeys.EMAIL_BEHAVIOR.rawValue)?.lowercase()) {
                    EEmailBehavior.REQUIRED.rawValue -> EmailBehavior.REQUIRED
                    EEmailBehavior.HIDDEN.rawValue -> EmailBehavior.HIDDEN
                    else -> EmailBehavior.OPTIONAL
                }
            }
            // Fallback на старый requireEmail для обратной совместимости
            paymentDataMap.hasKey(EPaymentConfigKeys.REQUIRE_EMAIL.rawValue) &&
                    paymentDataMap.getBoolean(EPaymentConfigKeys.REQUIRE_EMAIL.rawValue) -> {
                EmailBehavior.REQUIRED
            }
            else -> EmailBehavior.OPTIONAL
        }

        // SDK 2.1.1: Обработка paymentMethodSequence
        val paymentMethodSequence = if (paymentDataMap.hasKey(EPaymentConfigKeys.PAYMENT_METHOD_SEQUENCE.rawValue)) {
            val sequenceArray = paymentDataMap.getArray(EPaymentConfigKeys.PAYMENT_METHOD_SEQUENCE.rawValue)
            parsePaymentMethodSequence(sequenceArray)
        } else {
            ArrayList()
        }

        // SDK 2.1.1: Обработка singlePaymentMode
        val singlePaymentMode = if (paymentDataMap.hasKey(EPaymentConfigKeys.SINGLE_PAYMENT_MODE.rawValue)) {
            parsePaymentMethod(paymentDataMap.getString(EPaymentConfigKeys.SINGLE_PAYMENT_MODE.rawValue))
        } else {
            null
        }

        // SDK 2.1.1: showResultScreenForSinglePaymentMode
        val showResultScreenForSinglePaymentMode = if (paymentDataMap.hasKey(EPaymentConfigKeys.SHOW_RESULT_SCREEN_FOR_SINGLE_PAYMENT_MODE.rawValue)) {
            paymentDataMap.getBoolean(EPaymentConfigKeys.SHOW_RESULT_SCREEN_FOR_SINGLE_PAYMENT_MODE.rawValue)
        } else {
            EDefaultValues.SHOW_RESULT_SCREEN_FOR_SINGLE_PAYMENT_MODE
        }

        val useDualMessagePayment = paymentDataMap.hasKey(EPaymentConfigKeys.USE_DUAL_MESSAGE_PAYMENT.rawValue) &&
                paymentDataMap.getBoolean(EPaymentConfigKeys.USE_DUAL_MESSAGE_PAYMENT.rawValue)

      val receipt: HashMap<String, Any?>? =
        if (paymentDataMap.hasKey(EPaymentConfigKeys.RECEIPT.rawValue)) {
          val rm = paymentDataMap.getMap(EPaymentConfigKeys.RECEIPT.rawValue)
          normalizeReceiptMap(readableMapToHashMap(rm))
        } else null

      val recurrent: CPRecurrent? =
        if (paymentDataMap.hasKey(EPaymentConfigKeys.RECURRENT.rawValue)) {
          val rcm = paymentDataMap.getMap(EPaymentConfigKeys.RECURRENT.rawValue)

          val interval =
            rcm?.getString("interval")
              ?: rcm?.getString("Interval")
              ?: "Month"

          val period =
            when {
              rcm?.hasKey("period") == true -> rcm.getInt("period")
              rcm?.hasKey("Period") == true -> rcm.getInt("Period")
              else -> 1
            }

          CPRecurrent(
            interval = interval,
            period = period,
            customerReceipt = receipt // ВАЖНО: в SDK это поле сериализуется как "receipt"
          )
        } else null

        val paymentData =  PaymentData(
            amount = paymentDataMap.getString(EPaymentConfigKeys.AMOUNT.rawValue) ?: "0",
            currency = paymentDataMap.getString(EPaymentConfigKeys.CURRENCY.rawValue) ?: ECurrency.RUB,
            description = paymentDataMap.getString(EPaymentConfigKeys.DESCRIPTION.rawValue),
            accountId = paymentDataMap.getString(EPaymentConfigKeys.ACCOUNT_ID.rawValue),
            email = paymentDataMap.getString(EPaymentConfigKeys.EMAIL.rawValue),
            payer = payer,
            receipt = receipt, // Информациюя для создания чека
            recurrent = recurrent,
          jsonData = jsonDataString
        )

        // SDK 2.1.1: Новая структура PaymentConfiguration
        return PaymentConfiguration(
            publicId = publicId,
            paymentData = paymentData,
            emailBehavior = emailBehavior,
            useDualMessagePayment = useDualMessagePayment,
            paymentMethodSequence = paymentMethodSequence,
            singlePaymentMode = singlePaymentMode,
            showResultScreenForSinglePaymentMode = showResultScreenForSinglePaymentMode
        )
    }

    /**
     * Парсинг массива способов оплаты из JS в ArrayList<String>
     * SDK 2.1.1 использует CPPaymentMethod константы
     */
    private fun parsePaymentMethodSequence(sequenceArray: ReadableArray?): ArrayList<String> {
        val result = ArrayList<String>()
        if (sequenceArray == null) return result

        for (i in 0 until sequenceArray.size()) {
            val method = sequenceArray.getString(i)
            val cpMethod = mapToCPPaymentMethod(method)
            if (cpMethod != null) {
                result.add(cpMethod)
            }
        }

        return result
    }

    /**
     * Парсинг одиночного способа оплаты из JS в CPPaymentMethod строку
     */
    private fun parsePaymentMethod(method: String?): String? {
        return mapToCPPaymentMethod(method)
    }

    /**
     * Маппинг JS способа оплаты в строковые константы SDK 2.1.1
     * Константы соответствуют CPPaymentMethod из документации SDK
     */
    private fun mapToCPPaymentMethod(method: String?): String? {
        return when (method?.lowercase()) {
            EPaymentMethodType.CARD.rawValue -> "card"
            EPaymentMethodType.TPAY.rawValue, EPaymentMethodType.TINKOFFPAY.rawValue -> "tpay"
            EPaymentMethodType.SBERPAY.rawValue -> "sberpay"
            EPaymentMethodType.SBP.rawValue -> "sbp"
            EPaymentMethodType.MIR_PAY.rawValue -> "mirpay"
            EPaymentMethodType.DOLYAME.rawValue -> "dolyame"
            else -> null
        }
    }

    /**
     * Конвертация результата SDK в WritableMap для JavaScript
     * SDK 2.1.1: Результат содержит status, transactionId, reasonCode
     *
     * @param status Статус транзакции
     * @param transactionId ID транзакции
     * @param reasonCode Код причины (для ошибок)
     * @return WritableMap для отправки в JavaScript
     */
    fun resultToWritableMap(
        status: CloudpaymentsSDK.TransactionStatus?,
        transactionId: Long?,
        reasonCode: Int
    ): WritableMap {
        return Arguments.createMap().apply {
            putBoolean(EResponseKeys.SUCCESS.rawValue, status == CloudpaymentsSDK.TransactionStatus.Succeeded)

            transactionId?.let {
                putDouble(EResponseKeys.TRANSACTION_ID.rawValue, it.toDouble())
            }

            status?.let { s ->
                putString(EResponseKeys.STATUS.rawValue, when (s) {
                    CloudpaymentsSDK.TransactionStatus.Succeeded -> EPaymentResultValues.SUCCEEDED.rawValue
                    CloudpaymentsSDK.TransactionStatus.Failed -> EDefaultMessages.PAYMENT_FAILED.rawValue
                })
            }

            if (reasonCode != 0) {
                putInt(EPaymentResultValues.REASON_CODE.rawValue, reasonCode)
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
     * @deprecated SDK 2.1.1 использует singlePaymentMode вместо SDKRunMode
     *
     * @param paymentMethod Строковое представление способа оплаты
     * @return CPPaymentMethod строка или null
     */
    fun getPaymentMethod(paymentMethod: String?): String? {
        return mapToCPPaymentMethod(paymentMethod)
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
