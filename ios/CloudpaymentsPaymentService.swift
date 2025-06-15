import Foundation
import React
import Cloudpayments

@objc(CloudpaymentsPaymentService)
public class CloudpaymentsPaymentService: NSObject {

  private var api: CloudpaymentsApi
  private let publicId: String
  private var intentResponses: [String: PaymentIntentResponse] = [:]


  @objc public init(publicId: String) {
    self.publicId = publicId
    self.api = CloudpaymentsApi(publicId: publicId)
    super.init()
  }

  @objc public static func requiresMainQueueSetup() -> Bool {
    return false
  }

  @objc(createIntent:resolve:reject:)
  public func createIntent(paymentData: [String: Any],
                    resolve: @escaping RCTPromiseResolveBlock,
                    reject: @escaping RCTPromiseRejectBlock) {
    guard let configuration = createPaymentConfiguration(from: paymentData) else {
        reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
        return
    }

    CloudpaymentsApi.createIntent(with: configuration) { response in
        if let response = response {
          self.storeIntentResponse(response)
            resolve(response.toDictionary())
        } else {
            reject("CREATE_INTENT_ERROR", "Failed to create payment intent", nil)
        }
    }
  }

  @objc(getTPayLink:paymentData:resolve:reject:)
  public func getTPayLink(puid: String, paymentData: [String: Any],
                   resolve: @escaping RCTPromiseResolveBlock,
                   reject: @escaping RCTPromiseRejectBlock) {
    
    guard let configuration = createPaymentConfiguration(from: paymentData) else {
        reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
        return
    }
    
    
    CloudpaymentsApi.getTPayLinkIntentApi(puid: puid, configuration: configuration) { statusCode, link in
      guard let link = link else {
        reject("TPAY_LINK_ERROR", "Failed to get TPay link", nil)
        return
      }
      resolve(["statusCode": statusCode ?? 0, "link": link])
    }
  }

  @objc(getSbpLink:schema:paymentData:resolve:reject:)
  public func getSbpLink(puid: String, schema: String, paymentData: [String: Any],
                  resolve: @escaping RCTPromiseResolveBlock,
                  reject: @escaping RCTPromiseRejectBlock) {
    guard let configuration = createPaymentConfiguration(from: paymentData) else {
        reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
        return
    }
    
    CloudpaymentsApi.getSbpLinkIntentApi(puid: puid, schema: schema, configuration: configuration) { statusCode, link in
      guard let link = link else {
        reject("SBP_LINK_ERROR", "Failed to get SBP link", nil)
        return
      }
      resolve(["statusCode": statusCode ?? 0, "link": link])
    }
  }

  @objc(getSberPayLink:paymentData:resolve:reject:)
  public func getSberPayLink(puid: String, paymentData: [String: Any],
                      resolve: @escaping RCTPromiseResolveBlock,
                      reject: @escaping RCTPromiseRejectBlock) {
    guard let configuration = createPaymentConfiguration(from: paymentData) else {
        reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
        return
    }
    
    CloudpaymentsApi.getSberPayLinkIntentApi(puid: puid, configuration: configuration) { statusCode, response in
      guard let response = response else {
        reject("SBERPAY_LINK_ERROR", "Failed to get SberPay link", nil)
        return
      }
      resolve(["statusCode": statusCode ?? 0, "response": response.toDictionary()])
    }
  }

  @objc(getMerchantConfiguration:resolve:reject:)
  public func getMerchantConfiguration(paymentData: [String: Any],
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock) {
    guard let configuration = createPaymentConfiguration(from: paymentData) else {
        reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
        return
    }
    
    CloudpaymentsApi.getMerchantConfiguration(configuration: configuration) { config in
      guard let config = config else {
        reject("MERCHANT_CONFIG_ERROR", "Failed to get merchant configuration", nil)
        return
      }
      resolve(config.toDictionary())
    }
  }

  @objc(getIntentWaitStatus:type:resolve:reject:)
  public func getIntentWaitStatus(paymentData: [String: Any], type: String,
                           resolve: @escaping RCTPromiseResolveBlock,
                           reject: @escaping RCTPromiseRejectBlock) {
    guard let configuration = createPaymentConfiguration(from: paymentData) else {
        reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
        return
    }
    
    let paymentMethodType: PaymentMethodType
    switch type.lowercased() {
    case "tpay", "tinkoffpay":
        paymentMethodType = .tpay
    case "sbp":
        paymentMethodType = .sbp
    case "sberpay":
      paymentMethodType = .sberPay
    default:
        reject("INVALID_PAYMENT_TYPE", "Unsupported payment type provided", nil)
        return
    }
    
    CloudpaymentsApi.getIntentWaitStatus(configuration, type: paymentMethodType) { statusCode in
      guard let statusCode = statusCode else {
        reject("INTENT_WAIT_STATUS_ERROR", "Failed to get intent wait status", nil)
        return
      }
      resolve(statusCode)
    }
  }
  
  @objc(getPublicKey:reject:)
  public func getPublicKey(resolve: @escaping RCTPromiseResolveBlock,
                           reject: @escaping RCTPromiseRejectBlock) {
      CloudpaymentsApi.getPublicKey { response, error in
          if let response = response {
              do {
                  let jsonData = try JSONEncoder().encode(response)
                  if let jsonString = String(data: jsonData, encoding: .utf8) {
                      resolve(jsonString)
                  } else {
                      reject("GET_PUBLIC_KEY_ERROR", "Failed to serialize public key response", nil)
                  }
              } catch {
                  reject("GET_PUBLIC_KEY_ERROR", error.localizedDescription, nil)
              }
          } else if let error = error {
              reject("GET_PUBLIC_KEY_ERROR", error.localizedDescription, nil)
          } else {
              reject("GET_PUBLIC_KEY_ERROR", "Unknown error", nil)
          }
      }
  }
  

  @objc(intentPatchById:patches:resolve:reject:)
  public func intentPatchById(paymentData: [String: Any], patches: [[String: Any]],
                       resolve: @escaping RCTPromiseResolveBlock,
                       reject: @escaping RCTPromiseRejectBlock) {
    guard let configuration = createPaymentConfiguration(from: paymentData) else {
        reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
        return
    }
    
    CloudpaymentsApi.intentPatchById(configuration: configuration, patches: patches) { response in
      guard let response = response else {
        reject("PATCH_INTENT_ERROR", "Failed to patch intent", nil)
        return
      }
      resolve(response.toDictionary())
    }
  }
  
  @objc(createIntentPay:cardCryptogram:intentId:resolve:reject:)
  public func createIntentPay(paymentData: NSDictionary, cardCryptogram: String,
                              intentId: String,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock) {

      guard let paymentDataDict = paymentData as? [String: Any],
            let configuration = createPaymentConfiguration(from: paymentDataDict) else {
          reject("CONFIGURATION_ERROR", "Invalid payment configuration", nil)
          return
      }
      
    guard let storedIntent = self.intentResponse(for: intentId) else {
            reject("INTENT_ID_NOT_FOUND", "IntentId \(intentId) was not found", nil)
            return
        }
    

      api.createIntentApiPay(cardCryptogram: cardCryptogram, with: configuration) { statusCode, response in
          guard let response = response else {
              reject(
                  "API_PAY_ERROR",
                  "Failed to process payment. Status code: \(statusCode.map { "\($0)" } ?? "unknown")",
                  NSError(domain: "CloudpaymentsSdk", code: statusCode ?? -1, userInfo: nil)
              )
              return
          }

          resolve([
              "statusCode": statusCode ?? 0,
              "response": response.toDictionary()
          ])
      }
  }

  
  
  private func createPaymentConfiguration(from paymentData: [String: Any]) -> PaymentConfiguration? {
      guard let publicId = paymentData["publicId"] as? String,
            let paymentDataObj = self.createPaymentData(from: paymentData) else {
          print("❌ Ошибка: нет publicId или данных платежа")
          return nil
      }

      print("✅ publicId в конфиге: \(publicId)")
    print("✅ paymentDataObj в конфиге: \(paymentDataObj)")

      return PaymentConfiguration(
          publicId: publicId,
          paymentData: paymentDataObj,
          delegate: nil,
          uiDelegate: nil,
          scanner: nil,
          requireEmail: paymentData["requireEmail"] as? Bool ?? false,
          useDualMessagePayment: paymentData["useDualMessagePayment"] as? Bool ?? false,
          disableApplePay: paymentData["disableApplePay"] as? Bool ?? true,
          successRedirectUrl: paymentData["successRedirectUrl"] as? String,
          failRedirectUrl: paymentData["failRedirectUrl"] as? String,
          saveCardSinglePaymentMode: paymentData["saveCardSinglePaymentMode"] as? Bool,
          showResultScreen: paymentData["showResultScreen"] as? Bool ?? false
      )
  }
  
  
  private func createPaymentData(from input: [String: Any]) -> PaymentData? {
      guard let amount = input["amount"] as? String,
            let currency = input["currency"] as? String else {
          return nil
      }
    
    let item = Receipt.Item(
                label: "test",
                price: 300.0,
                quantity: 3.0,
                amount: 900.0,
                vat: 20,
                method: 0,
                object: 0)
    
    let receipt = Receipt(
                items: [item],
                taxationSystem: 0,
                email: "test@test.ru",
                phone: "+79991112233",
                isBso: false,
                amounts: Receipt.Amounts(
                    electronic: 900.0,
                    advancePayment: 0.0,
                    credit: 0.0,
                    provision: 0.0))
    let recurrent = Recurrent(
                interval: "Month",
                period: 1,
                customerReceipt: receipt,
                amount: 100)

    let payer = PaymentDataPayer(firstName: "Test", lastName: "Testov", middleName: "Testovich", birth: "1955-02-22", address: "home 6", street: "Testovaya", city: "Moscow", country: "RU", phone: "89991234567", postcode: "12345")


      let paymentData = PaymentData()
          .setAmount(amount)
          .setCurrency(currency)
          .setPayer(payer)
          .setReceipt(receipt) // Данные для чека
        .setRecurrent(recurrent) // Данные для подписки
        .setCultureName("RU-ru")
    

      if let description = input["description"] as? String {
          paymentData.setDescription(description)
      }

      if let email = input["email"] as? String {
          paymentData.setEmail(email)
      }

      if let accountId = input["accountId"] as? String {
          paymentData.setAccountId(accountId)
      }

      if let jsonData = input["jsonData"] as? String {
          paymentData.setJsonData(jsonData)
      }

      return paymentData
  }
  
  private func storeIntentResponse(_ response: PaymentIntentResponse) {
      if let intentId = response.publicIntentId {
          intentResponses[intentId] = response
      }
  }
  private func intentResponse(for intentId: String) -> PaymentIntentResponse? {
      return intentResponses[intentId]
  }
  
  
}

extension PaymentIntentResponse {
    func toDictionary() -> [String: Any] {
        do {
            let data = try JSONEncoder().encode(self)
            if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                return json
            }
        } catch {
            print("Error converting PaymentIntentResponse to dictionary: \(error)")
        }
        return [:]
    }
}


extension SberPayResponse {
    func toDictionary() -> [String: Any] {
        do {
            let data = try JSONEncoder().encode(self)
            if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                return json
            }
        } catch {
            print("Error converting SberPayResponse to dictionary: \(error)")
        }
        return [:]
    }
}

extension ButtonConfiguration {
    func toDictionary() -> [String: Any] {
        return [
            "isOnTPayButton": isOnTPayButton,
            "isOnSbpButton": isOnSbpButton,
            "isOnSberPayButton": isOnSberPayButton,
            "successRedirectUrl": successRedirectUrl ?? NSNull(),
            "failRedirectUrl": failRedirectUrl ?? NSNull(),
        ]
    }
}

extension PaymentIntentResponse {
    var publicIntentId: String? {
        guard let data = try? JSONEncoder().encode(self),
              let json = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
              let intentId = json["id"] as? String else {
            return nil
        }
        return intentId
    }
}
