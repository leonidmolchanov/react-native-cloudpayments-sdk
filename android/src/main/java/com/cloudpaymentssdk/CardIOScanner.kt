package com.cloudpaymentssdk

import android.content.Context
import android.content.Intent
import android.graphics.Color
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import kotlinx.parcelize.Parcelize
import ru.cloudpayments.sdk.scanner.CardScanner
import ru.cloudpayments.sdk.scanner.CardData
import com.facebook.react.bridge.ReadableMap

/**
 * CardIO сканер для CloudPayments SDK с полной кастомизацией
 * Поддерживает все настройки из JavaScript конфигурации
 */
@Parcelize
class CardIOScanner(
    private val requireExpiry: Boolean = true,
    private val requireCVV: Boolean = false,
    private val requirePostalCode: Boolean = false,
    private val requireCardholderName: Boolean = false,
    private val hideCardIOLogo: Boolean = true,
    private val usePayPalLogo: Boolean = false,
    private val suppressManualEntry: Boolean = false,
    private val actionBarColor: String? = null,
    private val guideColor: String? = null,
    private val language: String? = null,
    private val suppressConfirmation: Boolean = false,
    private val suppressScan: Boolean = false,
    private val keepApplicationTheme: Boolean = false
): CardScanner() {
    
    companion object {
        /**
         * Создает CardIOScanner из JavaScript конфигурации
         */
        fun fromJSConfig(configMap: ReadableMap?): CardIOScanner {
            if (configMap == null) {
                return CardIOScanner() // Используем настройки по умолчанию
            }

            return CardIOScanner(
                requireExpiry = configMap.getBooleanOrDefault(ECardIOConfigKeys.REQUIRE_EXPIRY.rawValue, true),
                requireCVV = configMap.getBooleanOrDefault(ECardIOConfigKeys.REQUIRE_CVV.rawValue, false),
                requirePostalCode = configMap.getBooleanOrDefault(ECardIOConfigKeys.REQUIRE_POSTAL_CODE.rawValue, false),
                requireCardholderName = configMap.getBooleanOrDefault(ECardIOConfigKeys.REQUIRE_CARDHOLDER_NAME.rawValue, false),
                hideCardIOLogo = configMap.getBooleanOrDefault(ECardIOConfigKeys.HIDE_CARDIO_LOGO.rawValue, true),
                usePayPalLogo = configMap.getBooleanOrDefault(ECardIOConfigKeys.USE_PAYPAL_LOGO.rawValue, false),
                suppressManualEntry = configMap.getBooleanOrDefault(ECardIOConfigKeys.SUPPRESS_MANUAL_ENTRY.rawValue, false),
                actionBarColor = configMap.getStringOrNull(ECardIOConfigKeys.ACTION_BAR_COLOR.rawValue),
                guideColor = configMap.getStringOrNull(ECardIOConfigKeys.GUIDE_COLOR.rawValue),
                language = configMap.getStringOrNull(ECardIOConfigKeys.LANGUAGE.rawValue),
                suppressConfirmation = configMap.getBooleanOrDefault(ECardIOConfigKeys.SUPPRESS_CONFIRMATION.rawValue, false),
                suppressScan = configMap.getBooleanOrDefault(ECardIOConfigKeys.SUPPRESS_SCAN.rawValue, false),
                keepApplicationTheme = configMap.getBooleanOrDefault(ECardIOConfigKeys.KEEP_APPLICATION_THEME.rawValue, false)
            )
        }

        /**
         * Вспомогательные методы для безопасного чтения из ReadableMap
         */
        private fun ReadableMap.getBooleanOrDefault(key: String, default: Boolean): Boolean {
            return if (hasKey(key)) getBoolean(key) else default
        }

        private fun ReadableMap.getStringOrNull(key: String): String? {
            return if (hasKey(key)) getString(key) else null
        }
        
        /**
         * Парсит цвет из hex строки в int
         */
        private fun parseColor(colorString: String?): Int? {
            if (colorString.isNullOrBlank()) return null
            
            return try {
                val cleanColor = if (colorString.startsWith("#")) {
                    colorString.substring(1)
                } else {
                    colorString
                }
                
                when (cleanColor.length) {
                    6 -> Color.parseColor("#$cleanColor")
                    8 -> Color.parseColor("#$cleanColor")
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    override fun getScannerIntent(context: Context): Intent {
        return Intent(context, CardIOActivity::class.java).apply {
            // ============================================================================
            // ОБЯЗАТЕЛЬНЫЕ ПОЛЯ КАРТЫ
            // ============================================================================
            
            putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, requireExpiry)
            putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, requireCVV)
            putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, requirePostalCode)
            putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, requireCardholderName)
            
            // ============================================================================
            // НАСТРОЙКИ ИНТЕРФЕЙСА
            // ============================================================================
            
            putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, hideCardIOLogo)
            putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, usePayPalLogo)
            putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, suppressManualEntry)
            putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, suppressConfirmation)
            putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, suppressScan)
            putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, keepApplicationTheme)
            
            // ============================================================================
            // ЦВЕТОВАЯ СХЕМА
            // ============================================================================
            
            actionBarColor?.let { colorString ->
                parseColor(colorString)?.let { color ->
                    // Попытка установить цвет ActionBar (может не поддерживаться в некоторых версиях CardIO)
                    try {
                        putExtra("io.card.payment.EXTRA_ACTIONBAR_COLOR", color)
                    } catch (e: Exception) {
                        // Игнорируем если не поддерживается
                    }
                }
            }
            
            guideColor?.let { colorString ->
                parseColor(colorString)?.let { color ->
                    // Попытка установить цвет направляющих (может не поддерживаться в некоторых версиях CardIO)
                    try {
                        putExtra("io.card.payment.EXTRA_GUIDE_COLOR", color)
                    } catch (e: Exception) {
                        // Игнорируем если не поддерживается
                    }
                }
            }
            
            // ============================================================================
            // ЛОКАЛИЗАЦИЯ
            // ============================================================================
            
            language?.let { lang ->
                putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, lang)
            }
        }
    }

    override fun getCardDataFromIntent(data: Intent) =
        if (data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            val scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT) as? CreditCard
            val month = (scanResult?.expiryMonth ?: 0).toString().padStart(2, '0')
            val yearString = scanResult?.expiryYear?.toString() ?: "00"
            val year = if (yearString.length > 2) {
                yearString.substring(yearString.lastIndex - 1)
            } else {
                yearString.padStart(2, '0')
            }
            val cardData = CardData(scanResult?.cardNumber, month, year, scanResult?.cardholderName)
            cardData
        } else {
            null
        }
} 