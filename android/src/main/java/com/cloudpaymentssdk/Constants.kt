package com.cloudpaymentssdk

/**
 * Constants for CloudPayments SDK Android
 * Equivalent to iOS Constants.swift to ensure platform consistency
 */

// MARK: - Error Codes

enum class ECloudPaymentsError(val rawValue: String) {
    // Payment Service Errors
    CONFIGURATION_ERROR("CONFIGURATION_ERROR"),
    CREATE_INTENT_ERROR("CREATE_INTENT_ERROR"),
    TPAY_LINK_ERROR("TPAY_LINK_ERROR"),
    SBP_LINK_ERROR("SBP_LINK_ERROR"),
    SBERPAY_LINK_ERROR("SBERPAY_LINK_ERROR"),
    MERCHANT_CONFIG_ERROR("MERCHANT_CONFIG_ERROR"),
    INVALID_PAYMENT_TYPE("INVALID_PAYMENT_TYPE"),
    INTENT_WAIT_STATUS_ERROR("INTENT_WAIT_STATUS_ERROR"),
    GET_PUBLIC_KEY_ERROR("GET_PUBLIC_KEY_ERROR"),
    PATCH_INTENT_ERROR("PATCH_INTENT_ERROR"),
    API_PAY_ERROR("API_PAY_ERROR"),
    INTENT_ID_NOT_FOUND("INTENT_ID_NOT_FOUND"),

    // Card Service Errors
    BANK_INFO_ERROR("BANK_INFO_ERROR"),

    // Payment Form Errors
    PAYMENT_FAILED("PAYMENT_FAILED"),
    SERVICE_UNINITIALIZED("SERVICE_UNINITIALIZED"),
    NO_VIEW_CONTROLLER("NO_VIEW_CONTROLLER")
}

// MARK: - Configuration Keys

enum class EPaymentConfigKeys(val rawValue: String) {
    PUBLIC_ID("publicId"),
    AMOUNT("amount"),
    CURRENCY("currency"),
    DESCRIPTION("description"),
    EMAIL("email"),
    ACCOUNT_ID("accountId"),
    JSON_DATA("jsonData"),
    REQUIRE_EMAIL("requireEmail"),
    USE_DUAL_MESSAGE_PAYMENT("useDualMessagePayment"),
    DISABLE_APPLE_PAY("disableApplePay"),
    APPLE_PAY_MERCHANT_ID("applePayMerchantId"),
    SUCCESS_REDIRECT_URL("successRedirectUrl"),
    FAIL_REDIRECT_URL("failRedirectUrl"),
    SAVE_CARD_SINGLE_PAYMENT_MODE("saveCardSinglePaymentMode"),
    SHOW_RESULT_SCREEN("showResultScreen")
}

// MARK: - Payment Methods

enum class EPaymentMethodType(val rawValue: String) {
    TPAY("tpay"),
    TINKOFFPAY("tinkoffpay"),
    SBP("sbp"),
    SBERPAY("sberpay")
}

// MARK: - Payment Form Events

enum class EPaymentFormEvent(val rawValue: String) {
    WILL_DISPLAY("willDisplay"),
    DID_DISPLAY("didDisplay"),
    WILL_HIDE("willHide"),
    DID_HIDE("didHide")
}

enum class EPaymentFormAction(val rawValue: String) {
    ACTION("action"),
    TRANSACTION("transaction"),
    STATUS("status")
}

enum class EPaymentFormEventName(val rawValue: String) {
    PAYMENT_FORM("PaymentForm"),
    PAYMENT_FORM_WILL_DISPLAY("PaymentFormWillDisplay"),
    PAYMENT_FORM_DID_DISPLAY("PaymentFormDidDisplay"),
    PAYMENT_FORM_WILL_HIDE("PaymentFormWillHide"),
    PAYMENT_FORM_DID_HIDE("PaymentFormDidHide"),
    PAYMENT_SUCCESS("PaymentSuccess"),
    PAYMENT_FAILED("PaymentFailed"),
    PAYMENT_CANCELLED("PaymentCancelled"),
    PAYMENT_PROGRESS("PaymentProgress"),
    PAYMENT_METHOD_SELECTED("PaymentMethodSelected")
}

// MARK: - Default Messages

enum class EDefaultMessages(val rawValue: String) {
    PAYMENT_COMPLETED_SUCCESSFULLY("Payment completed successfully"),
    PAYMENT_COMPLETED_INTENT_API("Payment completed successfully (Intent API)"),
    PAYMENT_FAILED("Payment failed"),
    UNKNOWN_ERROR("Unknown error"),
    NO_VIEW_CONTROLLER_AVAILABLE("No view controller available"),
    INVALID_PAYMENT_CONFIGURATION("Invalid payment configuration"),
    PAYMENT_SERVICE_NOT_INITIALIZED("Payment service not initialized"),
    FAILED_TO_CREATE_PAYMENT_DATA("❌ Не удалось создать PaymentData из словаря"),
    PAYMENT_CANCELLED_BY_USER("Платеж отменен пользователем")
}

// MARK: - Default Values

object EDefaultValues {
    const val REQUIRE_EMAIL: Boolean = false
    const val USE_DUAL_MESSAGE_PAYMENT: Boolean = false
    const val DISABLE_APPLE_PAY: Boolean = true
    const val SHOW_RESULT_SCREEN: Boolean = false
    const val DEFAULT_TRANSACTION_ID: Long = 0L
}

// MARK: - Response Keys

enum class EResponseKeys(val rawValue: String) {
    SUCCESS("success"),
    TRANSACTION_ID("transactionId"),
    MESSAGE("message"),
    ERROR("error"),
    STATUS_CODE("statusCode"),
    LINK("link"),
    RESPONSE("response"),
    BANK_NAME("bankName"),
    LOGO_URL("logoUrl"),
    STATUS("status"),
    ERROR_CODE("errorCode")
}

// MARK: - Payer Data Keys

enum class EPayerDataKeys(val rawValue: String) {
    PAYER("payer"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    MIDDLE_NAME("middleName"),
    BIRTH("birth"),
    ADDRESS("address"),
    STREET("street"),
    CITY("city"),
    COUNTRY("country"),
    PHONE("phone"),
    POSTCODE("postcode")
}

// MARK: - Payment Result Values

enum class EPaymentResultValues(val rawValue: String) {
    SUCCEEDED("succeeded"),
    CANCELLED("cancelled"),
    CARD_MASK("cardMask"),
    AMOUNT("amount"),
    REASON_CODE("reasonCode"),
    DETAILS("details"),
    EXTERNAL_ID("externalId")
}

// MARK: - Default Currency

object ECurrency {
    const val RUB = "RUB"
}

// MARK: - Module Names

object EModuleNames {
    const val CLOUDPAYMENTS_SDK = "CloudpaymentsSdk"
    const val TAG = "CloudpaymentsSdkModule"
    const val PAYMENT_REQUEST_CODE = 12345
}

// MARK: - Android Specific Constants

object EAndroidSpecific {
    const val ACTIVITY_NOT_AVAILABLE = "ACTIVITY_NOT_AVAILABLE"
    const val SUCCESS_PROCESSING_ERROR = "SUCCESS_PROCESSING_ERROR"
    const val FAILED_PROCESSING_ERROR = "FAILED_PROCESSING_ERROR"
    const val PAYMENT_FORM_ERROR = "PAYMENT_FORM_ERROR"
}

// MARK: - CardIO Configuration Keys

enum class ECardIOConfigKeys(val rawValue: String) {
    REQUIRE_EXPIRY("requireExpiry"),
    REQUIRE_CVV("requireCVV"),
    REQUIRE_POSTAL_CODE("requirePostalCode"),
    REQUIRE_CARDHOLDER_NAME("requireCardholderName"),
    HIDE_CARDIO_LOGO("hideCardIOLogo"),
    USE_PAYPAL_LOGO("usePayPalLogo"),
    SUPPRESS_MANUAL_ENTRY("suppressManualEntry"),
    ACTION_BAR_COLOR("actionBarColor"),
    GUIDE_COLOR("guideColor"),
    LANGUAGE("language"),
    SUPPRESS_CONFIRMATION("suppressConfirmation"),
    SUPPRESS_SCAN("suppressScan"),
    KEEP_APPLICATION_THEME("keepApplicationTheme")
}

// MARK: - CardIO Constants

object ECardIOConstants {
    const val ENABLE_CARD_SCANNER = "enableCardScanner"
    const val CARD_SCANNER_CONFIG = "cardScannerConfig"
}
