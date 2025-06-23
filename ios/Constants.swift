import Foundation

// MARK: - Error Codes

enum ECloudPaymentsError: String, CaseIterable {
    // Payment Service Errors
    case configurationError = "CONFIGURATION_ERROR"
    case createIntentError = "CREATE_INTENT_ERROR"
    case tpayLinkError = "TPAY_LINK_ERROR"
    case sbpLinkError = "SBP_LINK_ERROR"
    case sberpayLinkError = "SBERPAY_LINK_ERROR"
    case merchantConfigError = "MERCHANT_CONFIG_ERROR"
    case invalidPaymentType = "INVALID_PAYMENT_TYPE"
    case intentWaitStatusError = "INTENT_WAIT_STATUS_ERROR"
    case getPublicKeyError = "GET_PUBLIC_KEY_ERROR"
    case patchIntentError = "PATCH_INTENT_ERROR"
    case apiPayError = "API_PAY_ERROR"
    case intentIdNotFound = "INTENT_ID_NOT_FOUND"
    
    // Card Service Errors
    case bankInfoError = "BANK_INFO_ERROR"
    
    // Payment Form Errors
    case paymentFailed = "PAYMENT_FAILED"
    case serviceUninitialized = "SERVICE_UNINITIALIZED"
    case noViewController = "NO_VIEW_CONTROLLER"
}

// MARK: - Configuration Keys

enum EPaymentConfigKeys: String, CaseIterable {
    case publicId = "publicId"
    case amount = "amount"
    case currency = "currency"
    case description = "description"
    case email = "email"
    case accountId = "accountId"
    case jsonData = "jsonData"
    case requireEmail = "requireEmail"
    case useDualMessagePayment = "useDualMessagePayment"
    case disableApplePay = "disableApplePay"
    case applePayMerchantId = "applePayMerchantId"
    case successRedirectUrl = "successRedirectUrl"
    case failRedirectUrl = "failRedirectUrl"
    case saveCardSinglePaymentMode = "saveCardSinglePaymentMode"
    case showResultScreen = "showResultScreen"
    case enableCardScanner = "enableCardScanner"

}

// MARK: - Payment Methods

enum EPaymentMethodType: String, CaseIterable {
    case tpay = "tpay"
    case tinkoffpay = "tinkoffpay"
    case sbp = "sbp"
    case sberpay = "sberpay"
}

// MARK: - Payment Form Events

enum EPaymentFormEvent: String, CaseIterable {
    case willDisplay = "willDisplay"
    case didDisplay = "didDisplay"
    case willHide = "willHide"
    case didHide = "didHide"
}

enum EPaymentFormAction: String, CaseIterable {
    case action = "action"
    case transaction = "transaction"
    case status = "status"
}

enum EPaymentFormEventName: String, CaseIterable {
    case paymentForm = "PaymentForm"
    case paymentFormWillDisplay = "PaymentFormWillDisplay"
    case paymentFormDidDisplay = "PaymentFormDidDisplay"
    case paymentFormWillHide = "PaymentFormWillHide"
    case paymentFormDidHide = "PaymentFormDidHide"
    case paymentSuccess = "PaymentSuccess"
    case paymentFailed = "PaymentFailed"
    case paymentCancelled = "PaymentCancelled"
    case paymentProgress = "PaymentProgress"
    case paymentMethodSelected = "PaymentMethodSelected"
}

// MARK: - Default Messages

enum EDefaultMessages: String, CaseIterable {
    case paymentCompletedSuccessfully = "Payment completed successfully"
    case paymentCompletedIntentApi = "Payment completed successfully (Intent API)"
    case paymentFailed = "Payment failed"
    case unknownError = "Unknown error"
    case noViewControllerAvailable = "No view controller available"
    case invalidPaymentConfiguration = "Invalid payment configuration"
    case paymentServiceNotInitialized = "Payment service not initialized"
    case failedToCreatePaymentData = "❌ Не удалось создать PaymentData из словаря"
}

// MARK: - Default Values

enum EDefaultValues {
    static let requireEmail: Bool = false
    static let useDualMessagePayment: Bool = false
    static let disableApplePay: Bool = true
    static let showResultScreen: Bool = false
    static let defaultTransactionId: Int64 = 0
}

// MARK: - Response Keys

enum EResponseKeys: String, CaseIterable {
    case success = "success"
    case transactionId = "transactionId"
    case message = "message"
    case error = "error"
    case statusCode = "statusCode"
    case link = "link"
    case response = "response"
    case bankName = "bankName"
    case logoUrl = "logoUrl"
} 
 