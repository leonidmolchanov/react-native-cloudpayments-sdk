import Foundation
import React
import Cloudpayments

@objc(CloudpaymentsPaymentFormService)
public class CloudpaymentsPaymentFormService: NSObject {

    private let CPSDK: CloudpaymentsSdkImpl?
    private let publicId: String

    // Callbacks для React Native
    private var onPaymentSuccess: RCTResponseSenderBlock?
    private var onPaymentFailure: RCTResponseSenderBlock?
    @objc public init(publicId: String, CPSDK: CloudpaymentsSdkImpl?) {
        self.CPSDK = CPSDK
        self.publicId = publicId
        super.init()
    }

    @objc public static func requiresMainQueueSetup() -> Bool {
        return false
    }

    // MARK: - Public Methods for React Native

    @objc(presentPaymentForm:resolve:reject:)
    public func presentPaymentForm(
        paymentData: [String: Any],
        resolve: @escaping RCTPromiseResolveBlock,
        reject: @escaping RCTPromiseRejectBlock
    ) {

        // Создаем callbacks для конвертации resolve/reject в RCTResponseSenderBlock
      let onSuccess: RCTResponseSenderBlock = { result in
          if let unwrappedResult = result, let resultDict = unwrappedResult.first as? [String: Any] {
              resolve(resultDict)
          } else {
              resolve([
                  EResponseKeys.success.rawValue: true,
                  EResponseKeys.message.rawValue: EDefaultMessages.paymentCompletedSuccessfully.rawValue
              ])
          }
      }

      let onFailure: RCTResponseSenderBlock = { error in
          if let unwrappedError = error, let errorMessage = unwrappedError.first as? String {
              reject(ECloudPaymentsError.paymentFailed.rawValue, errorMessage, nil)
          } else {
              reject(ECloudPaymentsError.paymentFailed.rawValue, EDefaultMessages.paymentFailed.rawValue, nil)
          }
      }

        DispatchQueue.main.async { [weak self] in
            self?.showPaymentForm(
                paymentData: paymentData,
                onSuccess: onSuccess,
                onFailure: onFailure
            )
        }
    }

    // MARK: - Private Implementation

    private func showPaymentForm(
        paymentData: [String: Any],
        onSuccess: @escaping RCTResponseSenderBlock,
        onFailure: @escaping RCTResponseSenderBlock
    ) {
        guard let topViewController = getTopViewController() else {
            onFailure([EDefaultMessages.noViewControllerAvailable.rawValue])
            return
        }

      guard let paymentObj = PaymentData(from: paymentData) else {
          return
      }

        // Сохраняем callbacks
        self.onPaymentSuccess = onSuccess
        self.onPaymentFailure = onFailure

      if let applePayMerchantId = paymentData[EPaymentConfigKeys.applePayMerchantId.rawValue] as? String, !applePayMerchantId.isEmpty {
          _ = paymentObj.setApplePayMerchantId(applePayMerchantId)
      }
      
      // Преобразуем requireEmail в emailBehavior
      let isRequireEmail = (paymentData[EPaymentConfigKeys.requireEmail.rawValue] as? Bool) ?? EDefaultValues.requireEmail
      var emailBehavior: EmailBehaviorType = isRequireEmail ? .required : .optional
      
      // Если передан emailBehavior напрямую
      if let emailBehaviorString = paymentData[EPaymentConfigKeys.emailBehavior.rawValue] as? String {
          switch emailBehaviorString.lowercased() {
          case "required":
              emailBehavior = .required
          case "hidden":
              emailBehavior = .hidden
          default:
              emailBehavior = .optional
          }
      }
      
      let useDualMessagePayment = (paymentData[EPaymentConfigKeys.useDualMessagePayment.rawValue] as? Bool) ?? EDefaultValues.useDualMessagePayment
      let disableApplePay = (paymentData[EPaymentConfigKeys.disableApplePay.rawValue] as? Bool) ?? EDefaultValues.disableApplePay

      // Парсим paymentMethodSequence (обязательный параметр, не опциональный)
      var paymentMethodSequence: [PaymentMethodType] = []
      if let sequenceArray = paymentData[EPaymentConfigKeys.paymentMethodSequence.rawValue] as? [String] {
          paymentMethodSequence = sequenceArray.compactMap { methodString in
              switch methodString.lowercased() {
              case "tpay", "tinkoffpay":
                  return .tpay
              case "card":
                  return .card
              case "sberpay":
                  return .sberPay
              case "sbp":
                  return .sbp
              case "dolyame":
                  return .dolyame
              default:
                  return nil
              }
          }
      }

      // Парсим singlePaymentMode
      var singlePaymentMode: PaymentMethodType? = nil
      if let singleModeString = paymentData[EPaymentConfigKeys.singlePaymentMode.rawValue] as? String {
          switch singleModeString.lowercased() {
          case "tpay", "tinkoffpay":
              singlePaymentMode = .tpay
          case "card":
              singlePaymentMode = .card
          case "sberpay":
              singlePaymentMode = .sberPay
          case "sbp":
              singlePaymentMode = .sbp
          case "dolyame":
              singlePaymentMode = .dolyame
          default:
              singlePaymentMode = nil
          }
      }

      let showResultScreenForSinglePaymentMode = (paymentData[EPaymentConfigKeys.showResultScreenForSinglePaymentMode.rawValue] as? Bool) ?? true

        let successRedirectUrl = (paymentData[EPaymentConfigKeys.successRedirectUrl.rawValue] as? String) ?? nil
        let failRedirectUrl = (paymentData[EPaymentConfigKeys.failRedirectUrl.rawValue] as? String) ?? nil

        // Создаем конфигурацию с правильным порядком параметров согласно SDK 2.1.0
        let configuration = PaymentConfiguration(
            publicId: publicId,
            paymentData: paymentObj,
            delegate: self,
            uiDelegate: self,
            emailBehavior: emailBehavior,
            paymentMethodSequence: paymentMethodSequence,
            singlePaymentMode: singlePaymentMode,
            useDualMessagePayment: useDualMessagePayment,
            disableApplePay: disableApplePay,
            showResultScreenForSinglePaymentMode: showResultScreenForSinglePaymentMode,
            successRedirectUrl: successRedirectUrl,
            failRedirectUrl: failRedirectUrl
        )

        // Показываем форму через новый API
        PaymentOptionsViewController.present(with: configuration, from: topViewController)
    }

    private func getTopViewController() -> UIViewController? {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first else {
            return nil
        }

        var topController = window.rootViewController
        while let presentedController = topController?.presentedViewController {
            topController = presentedController
        }

        return topController
    }
}

// MARK: - PaymentDelegate

extension CloudpaymentsPaymentFormService: PaymentDelegate {

    public func onPaymentFinished(_ transactionId: Int64?) {
        DispatchQueue.main.async { [weak self] in
            let result: [String: Any] = [
                EResponseKeys.success.rawValue: true,
                EResponseKeys.transactionId.rawValue: transactionId ?? EDefaultValues.defaultTransactionId,
                EResponseKeys.message.rawValue: EDefaultMessages.paymentCompletedSuccessfully.rawValue
            ]
          self?.CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [
             EPaymentFormAction.action.rawValue: EPaymentFormAction.transaction.rawValue,
              EResponseKeys.statusCode.rawValue: true,
              EResponseKeys.transactionId.rawValue: transactionId as Any
          ])
            self?.onPaymentSuccess?([result])
            self?.cleanup()
        }
    }

    public func onPaymentFailed(_ errorMessage: String?) {
        DispatchQueue.main.async { [weak self] in
            let error = errorMessage ?? EDefaultMessages.paymentFailed.rawValue
          self?.CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [
             EPaymentFormAction.action.rawValue: EPaymentFormAction.transaction.rawValue,
              EResponseKeys.statusCode.rawValue: false,
              EResponseKeys.message.rawValue: errorMessage ?? EDefaultMessages.unknownError.rawValue
          ])
            self?.onPaymentFailure?([error])
            self?.cleanup()
        }
    }

    public func onPaymentClosed() {
        DispatchQueue.main.async { [weak self] in
            // Платеж был закрыт пользователем без завершения
            self?.CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [
                EPaymentFormAction.action.rawValue: EPaymentFormAction.transaction.rawValue,
                EResponseKeys.statusCode.rawValue: false,
                EResponseKeys.message.rawValue: "Payment closed by user"
            ])
            self?.cleanup()
        }
    }

}

// MARK: - PaymentUIDelegate

extension CloudpaymentsPaymentFormService: PaymentUIDelegate {

    public func paymentFormWillDisplay() {
      CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [EPaymentFormAction.action.rawValue: EPaymentFormEvent.willDisplay.rawValue])
    }

    public func paymentFormDidDisplay() {
      CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [EPaymentFormAction.action.rawValue: EPaymentFormEvent.didDisplay.rawValue])
    }

    public func paymentFormWillHide() {
      CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [EPaymentFormAction.action.rawValue: EPaymentFormEvent.willHide.rawValue])
    }

    public func paymentFormDidHide() {
      CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [EPaymentFormAction.action.rawValue: EPaymentFormEvent.didHide.rawValue])
        cleanup()
    }
}


// MARK: - Helper Methods

extension CloudpaymentsPaymentFormService {

    private func cleanup() {
        onPaymentSuccess = nil
        onPaymentFailure = nil
    }
}
