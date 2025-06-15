package com.cloudpaymentssdk

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import ru.cloudpayments.sdk.scanner.CardData
import ru.cloudpayments.sdk.scanner.CardScanner
import com.facebook.react.bridge.ReadableMap

/**
 * Конфигурируемый CardIO сканер для CloudPayments SDK
 * 
 * @description Реализация сканера банковских карт с использованием CardIO библиотеки.
 * Поддерживает полную настройку через JavaScript конфигурацию.
 * 
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */
@Parcelize
class CardIOScanner(
    private val config: CardIOConfiguration = CardIOConfiguration()
) : CardScanner(), Parcelable {

    /**
     * Конфигурация CardIO сканера
     */
    @Parcelize
    data class CardIOConfiguration(
        val requireExpiry: Boolean = true,
        val requireCVV: Boolean = false,
        val requirePostalCode: Boolean = false,
        val requireCardholderName: Boolean = false,
        val hideCardIOLogo: Boolean = true,
        val usePayPalLogo: Boolean = false,
        val suppressManualEntry: Boolean = false,
        val actionBarColor: String? = null,
        val guideColor: String? = null,
        val language: String? = null,
        val suppressConfirmation: Boolean = false,
        val suppressScan: Boolean = false,
        val keepApplicationTheme: Boolean = false
    ) : Parcelable

    companion object {
        /**
         * Создает CardIOScanner из JavaScript конфигурации
         */
        fun fromJSConfig(configMap: ReadableMap?): CardIOScanner {
            if (configMap == null) {
                return CardIOScanner() // Используем конфигурацию по умолчанию
            }

            val config = CardIOConfiguration(
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

            return CardIOScanner(config)
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
    }

    /**
     * Создает Intent для запуска CardIO сканера с применением конфигурации
     */
    override fun getScannerIntent(context: Context): Intent {
        return Intent(context, CardIOActivity::class.java).apply {
            // ============================================================================
            // ОБЯЗАТЕЛЬНЫЕ ПОЛЯ КАРТЫ
            // ============================================================================
            
            putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, config.requireExpiry)
            putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, config.requireCVV)
            putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, config.requirePostalCode)
            putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, config.requireCardholderName)
            
            // ============================================================================
            // НАСТРОЙКИ ИНТЕРФЕЙСА
            // ============================================================================
            
            putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, config.hideCardIOLogo)
            putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, config.usePayPalLogo)
            putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, config.suppressManualEntry)
            
            // ============================================================================
            // ЦВЕТОВАЯ СХЕМА
            // ============================================================================
            
            // Цвета могут не поддерживаться в данной версии CardIO
            config.actionBarColor?.let { colorString ->
                try {
                    val color = Color.parseColor(colorString)
                    // Цветовая настройка будет добавлена в будущих версиях CardIO
                } catch (e: IllegalArgumentException) {
                    // Игнорируем некорректные цвета
                }
            }
            
            config.guideColor?.let { colorString ->
                try {
                    val color = Color.parseColor(colorString)
                    // Цветовая настройка будет добавлена в будущих версиях CardIO
                } catch (e: IllegalArgumentException) {
                    // Игнорируем некорректные цвета
                }
            }
            
            // ============================================================================
            // ЛОКАЛИЗАЦИЯ
            // ============================================================================
            
            config.language?.let { lang ->
                putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, lang)
            }
            
            // ============================================================================
            // ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ
            // ============================================================================
            
            // Дополнительные настройки будут добавлены в будущих версиях CardIO
        }
    }
    
    /**
     * Извлекает данные карты из результата CardIO сканера
     * 
     * @param data Intent с результатами сканирования
     * @return CardData объект с данными карты или null если сканирование отменено
     */
    override fun getCardDataFromIntent(data: Intent): CardData? {
        return if (data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            val scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT) as? CreditCard
            
            if (scanResult != null) {
                // Форматируем месяц (добавляем ведущий ноль если нужно)
                val month = (scanResult.expiryMonth ?: 0).toString().padStart(2, '0')
                
                // Форматируем год (берем последние 2 цифры)
                val yearString = scanResult.expiryYear?.toString() ?: "00"
                val year = if (yearString.length > 2) {
                    yearString.substring(yearString.lastIndex - 1)
                } else {
                    yearString.padStart(2, '0')
                }
                
                // Создаем объект CardData
                CardData(
                    scanResult.cardNumber,
                    month,
                    year,
                    scanResult.cardholderName
                )
            } else {
                null
            }
        } else {
            // Сканирование было отменено пользователем
            null
        }
    }
} 