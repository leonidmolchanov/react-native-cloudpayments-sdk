import Foundation
import React
import Cloudpayments

@objc(CloudpaymentsCardService)
public class CloudpaymentsCardService: NSObject {

  @objc public static func requiresMainQueueSetup() -> Bool {
      return false
  }


  @objc(isCardNumberValid:resolve:reject:)
    public func isCardNumberValid(_ cardNumber: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {

        resolve(Card.isCardNumberValid(cardNumber))
    }

  // Проверка корректности срока действия карты
  @objc(isExpDateValid:resolve:reject:)
  public func isExpDateValid(_ expDate: String?,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) {
    resolve(Card.isExpDateValid(expDate))
  }

  // Проверка корректности CVV кода
  @objc(isValidCvv:isCvvRequired:resolve:reject:)
  public func isValidCvv(_ cvv: String?,
                         isCvvRequired: Bool,
                         resolve: @escaping RCTPromiseResolveBlock,
                         reject: @escaping RCTPromiseRejectBlock) {
    resolve(Card.isValidCvv(cvv: cvv, isCvvRequired: isCvvRequired))
  }

  // Определение типа платежной системы по номеру карты
  @objc(cardTypeFromCardNumber:resolve:reject:)
  public func cardType(from cardNumber: String,
                       resolve: @escaping RCTPromiseResolveBlock,
                       reject: @escaping RCTPromiseRejectBlock) {
    resolve(Card.cardType(from: cardNumber).toString())
  }
  
  @objc(makeCardCryptogramPacket:expDate:cvv:merchantPublicID:publicKey:keyVersion:resolve:reject:)
  public func makeCardCryptogramPacket(cardNumber: String, expDate: String, cvv: String, merchantPublicID: String, publicKey: String, keyVersion: Int,
                                       resolve: @escaping RCTPromiseResolveBlock,
                                       reject: @escaping RCTPromiseRejectBlock) {
      let cryptogram = Card.makeCardCryptogramPacket(cardNumber: cardNumber, expDate: expDate, cvv: cvv, merchantPublicID: merchantPublicID, publicKey: publicKey, keyVersion: keyVersion)
      resolve(cryptogram)
  }

  @objc(getBankInfo:resolve:reject:)
  public func getBankInfo(cardNumber: String,
                          resolve: @escaping RCTPromiseResolveBlock,
                          reject: @escaping RCTPromiseRejectBlock) {
      CloudpaymentsApi.getBankInfo(cardNumber: cardNumber) { info, error in
          if let error = error {
              reject("BANK_INFO_ERROR", error.localizedDescription, error)
          } else if let info = info {
              resolve(["bankName": "\(info)"]) // временное решение, проверь что info выводит
          } else {
              resolve(["bankName": ""])
          }
      }
  }
 
}
