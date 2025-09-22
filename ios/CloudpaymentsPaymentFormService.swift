import Foundation
import React
import Cloudpayments

@objc(CloudpaymentsPaymentFormService)
public class CloudpaymentsPaymentFormService: NSObject {

    private let CPSDK: CloudpaymentsSdkImpl?
    private let publicId: String
    private var currentPaymentForm: PaymentForm?

    // Callbacks для React Native
    private var onPaymentSuccess: RCTResponseSenderBlock?
    private var onPaymentFailure: RCTResponseSenderBlock?
    var scanner: CloudpaymentsCardIOService?
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
                paymentData:paymentData,
                onSuccess: onSuccess,
                onFailure: onFailure
            )
        }
    }

    // MARK: - Private Implementation

    private func showPaymentForm(
        paymentData:[String: Any],
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
          paymentObj.setApplePayMerchantId(applePayMerchantId)
      }
      let isApplePayDisabled = (paymentData[EPaymentConfigKeys.disableApplePay.rawValue] as? Bool) ?? EDefaultValues.disableApplePay
      let isRequireEmail = (paymentData[EPaymentConfigKeys.requireEmail.rawValue] as? Bool) ?? EDefaultValues.requireEmail
      let useDualMessagePayment = (paymentData[EPaymentConfigKeys.useDualMessagePayment.rawValue] as? Bool) ?? EDefaultValues.useDualMessagePayment

      let isUseScanner = (paymentData[EPaymentConfigKeys.enableCardScanner.rawValue] as? Bool) ?? false
      self.scanner = isUseScanner ? CloudpaymentsCardIOService(config: [:]) : nil


        let successRedirectUrl = (paymentData[EPaymentConfigKeys.successRedirectUrl.rawValue] as? String) ?? nil
        let failRedirectUrl = (paymentData[EPaymentConfigKeys.failRedirectUrl.rawValue] as? String) ?? nil

        // Создаем конфигурацию
        let configuration = PaymentConfiguration(
            publicId: publicId,
            paymentData: paymentObj,
            delegate: self,
            uiDelegate: self,
            scanner:scanner,
            requireEmail: isRequireEmail,
            useDualMessagePayment: useDualMessagePayment,
            disableApplePay: isApplePayDisabled,
            successRedirectUrl: successRedirectUrl,
            failRedirectUrl: failRedirectUrl
        )

        // Показываем форму
        currentPaymentForm = PaymentForm.present(with: configuration, from: topViewController)
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

    public func paymentFinishedIntentApi(_ transaction: Int64?) {
        DispatchQueue.main.async { [weak self] in
            let result: [String: Any] = [
                EResponseKeys.success.rawValue: true,
                EResponseKeys.transactionId.rawValue: transaction ?? EDefaultValues.defaultTransactionId,
                EResponseKeys.message.rawValue: EDefaultMessages.paymentCompletedIntentApi.rawValue
            ]
          self?.CPSDK?.sendEvent(name: EPaymentFormEventName.paymentForm.rawValue, data: [
              EResponseKeys.statusCode.rawValue: true,
              EPaymentFormAction.action.rawValue: EPaymentFormAction.transaction.rawValue,
              EResponseKeys.transactionId.rawValue: transaction as Any
          ])
            self?.onPaymentSuccess?([result])
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
        currentPaymentForm = nil
        onPaymentSuccess = nil
        onPaymentFailure = nil
    }
}
