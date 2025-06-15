import CloudpaymentsSdk from '../NativeCloudpaymentsSdk';
import type {
  IPaymentService,
  IPaymentData,
  ICreateIntentPaymentData,
  EPaymentMethodType,
  IPaymentFormResponse,
  TIntentWaitStatus,
} from '../types';

export const PaymentService: IPaymentService = {
  async init(publicId: string): Promise<void> {
    await CloudpaymentsSdk.initialize(publicId);
  },

  createIntent(paymentData: ICreateIntentPaymentData) {
    return CloudpaymentsSdk.createIntent(paymentData);
  },

  async getIntentWaitStatus(
    paymentData: ICreateIntentPaymentData,
    paymentMethod: EPaymentMethodType
  ): Promise<IPaymentFormResponse> {
    try {
      const statusCode: TIntentWaitStatus =
        await CloudpaymentsSdk.getIntentWaitStatus(paymentData, paymentMethod);

      // Преобразуем статус код в IPaymentFormResponse
      if (statusCode === 200) {
        return {
          success: true,
          message: 'Платеж успешно завершен',
          transactionId: undefined, // Нативный метод не возвращает ID транзакции
        };
      } else {
        return {
          success: false,
          message: `Ошибка платежа. Код: ${statusCode}`,
          transactionId: undefined,
        };
      }
    } catch (error) {
      return {
        success: false,
        message: error instanceof Error ? error.message : 'Неизвестная ошибка',
        transactionId: undefined,
      };
    }
  },

  presentPaymentForm(paymentData: IPaymentData) {
    return CloudpaymentsSdk.presentPaymentForm(paymentData);
  },
};
