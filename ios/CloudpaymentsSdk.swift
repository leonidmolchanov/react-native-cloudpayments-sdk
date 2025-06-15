import Foundation
import React

@objc(CloudpaymentsSdkImpl)
public class CloudpaymentsSdkImpl: NSObject {

  @objc public static func requiresMainQueueSetup() -> Bool {
      return false
  }
  
  weak var eventEmitter: RCTEventEmitter?

  @objc public let cardService = CloudpaymentsCardService()
  @objc public var paymentService: CloudpaymentsPaymentService?
  @objc public var paymentFormService: CloudpaymentsPaymentFormService?
  
  @objc(setEventEmitter:)
  public func setEventEmitter(_ emitter: RCTEventEmitter) {
          self.eventEmitter = emitter
          // Передать eventEmitter в paymentFormService
      self.eventEmitter = emitter
      }
  @objc(sendEvent:name:)
  public func sendEvent(name: String, data: [String: Any]) {
        print("ACTION \(data)")
          eventEmitter?.sendEvent(withName: name, body: data)
      }

  @objc(initializeWithPublicId:)
  public func initializeWithPublicId(publicId: String) {
      paymentService = CloudpaymentsPaymentService(publicId: publicId)
    paymentFormService = CloudpaymentsPaymentFormService(publicId: publicId, CPSDK: self)
  }

}
