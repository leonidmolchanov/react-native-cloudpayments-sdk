#import "CloudpaymentsSdk.h"
#import <CardIO/CardIO.h>

#if __has_include("CloudpaymentsSdk-Swift.h")
#import "CloudpaymentsSdk-Swift.h"
#else
#import "CloudpaymentsSdk/CloudpaymentsSdk-Swift.h"
#endif


@interface CloudpaymentsSdk ()
@property(nonatomic, strong) CloudpaymentsSdkImpl *sdkImpl;
@end


@implementation CloudpaymentsSdk

RCT_EXPORT_MODULE()

- (instancetype)init {
    if (self = [super init]) {
        _sdkImpl = [CloudpaymentsSdkImpl new];
    }
    return self;
}

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"PaymentForm",
        @"Payment",
        @"Card",
        @"3DS",
    ];
}


RCT_EXPORT_METHOD(initialize:(NSString *)publicId
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.sdkImpl initializeWithPublicId:publicId];
    [self.sdkImpl setEventEmitter:self];
    resolve(@(YES));
  });
}



RCT_EXPORT_METHOD(isCardNumberValid:(NSString *)cardNumber
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.sdkImpl.cardService isCardNumberValid:cardNumber resolve:resolve reject:reject];
      });
}

RCT_EXPORT_METHOD(isExpDateValid:(NSString *)expDate
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.sdkImpl.cardService isExpDateValid:expDate resolve:resolve reject:reject];
    });
}

RCT_EXPORT_METHOD(isValidCvv:(NSString *)cvv
                  isCvvRequired:(BOOL)isCvvRequired
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.sdkImpl.cardService isValidCvv:cvv isCvvRequired:isCvvRequired resolve:resolve reject:reject];
    });
}

RCT_EXPORT_METHOD(cardTypeFromCardNumber:(NSString *)cardNumber
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.sdkImpl.cardService cardTypeFromCardNumber:cardNumber resolve:resolve reject:reject];
    });
}

RCT_EXPORT_METHOD(makeCardCryptogramPacket:(NSString *)cardNumber
                  expDate:(NSString *)expDate
                  cvv:(NSString *)cvv
                  merchantPublicID:(NSString *)merchantPublicID
                  publicKey:(NSString *)publicKey
                  keyVersion:(double)keyVersion
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.sdkImpl.cardService makeCardCryptogramPacket:cardNumber
                                                             expDate:expDate
                                                                 cvv:cvv
                                                    merchantPublicID:merchantPublicID
                                                           publicKey:publicKey
                                                          keyVersion:(NSInteger)keyVersion
                                                             resolve:resolve
                                                              reject:reject];
  });
}

RCT_EXPORT_METHOD(getBankInfo:(NSString *)cardNumber
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.sdkImpl.cardService getBankInfo:cardNumber
                                                resolve:resolve
                                                 reject:reject];
  });
}










RCT_EXPORT_METHOD(createIntent:(NSDictionary *)paymentData
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
      if (self.sdkImpl.paymentService) {
          [self.sdkImpl.paymentService createIntent:paymentData
                                       resolve:resolve
                                       reject:reject];
      } else {
          reject(@"SERVICE_UNINITIALIZED", @"Payment service not initialized", nil);
      }
  });
}
RCT_EXPORT_METHOD(createIntentApiPay:(NSDictionary *)paymentData
                  cardCryptogram:(NSString *)cardCryptogram
                  intentId:(NSString *)intentId
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.sdkImpl.paymentService createIntentPay:paymentData
                                     cardCryptogram:cardCryptogram
                                     intentId:intentId
                                     resolve:resolve
                                     reject:reject];
  });
}


RCT_EXPORT_METHOD(getPublicKey:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
      if (self.sdkImpl.paymentService) {
          [self.sdkImpl.paymentService getPublicKey:resolve reject:reject];
      } else {
          reject(@"SERVICE_UNINITIALIZED", @"Payment service not initialized", nil);
      }
  });
}











RCT_EXPORT_METHOD(presentPaymentForm:(NSDictionary *)paymentData
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
  dispatch_async(dispatch_get_main_queue(), ^{
      if (self.sdkImpl.paymentFormService) {
        [self.sdkImpl.paymentFormService presentPaymentForm:paymentData
                                     resolve:resolve
                                     reject:reject];
      } else {
          reject(@"SERVICE_UNINITIALIZED", @"Payment service not initialized", nil);
      }
  });
}


@end
