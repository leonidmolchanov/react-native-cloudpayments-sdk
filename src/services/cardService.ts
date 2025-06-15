import CloudpaymentsSdk from '../NativeCloudpaymentsSdk';
import { isValidExpDate } from '../utils';
import type { ICardService, IBankInfo, IPublicKeyResponse } from '../types';

export const CardService: ICardService = {
  async isValidCardNumber(cardNumber: string): Promise<boolean> {
    return CloudpaymentsSdk.isCardNumberValid(cardNumber);
  },

  getBankInfo(cardNumber: string): Promise<IBankInfo> {
    //@ts-ignore
    return CloudpaymentsSdk.getBankInfo(cardNumber);
  },

  async getPublicKey(): Promise<IPublicKeyResponse> {
    const pemKey = await CloudpaymentsSdk.getPublicKey();
    // Преобразуем строку в объект IPublicKeyResponse
    return {
      Pem: pemKey,
      Version: 1, // Версия по умолчанию, так как нативный метод возвращает только PEM
    };
  },
};

// Дополнительные методы, не входящие в основной интерфейс
export const CardUtils = {
  isExpDateValid(expDate: string) {
    return CloudpaymentsSdk.isExpDateValid(expDate);
  },

  isValidCvv(cvv: string, isCvvRequired: boolean) {
    return CloudpaymentsSdk.isValidCvv(cvv, isCvvRequired);
  },

  isValidExpDate,

  makeCardCryptogramPacket(
    cardNumber: string,
    expDate: string,
    cvv: string,
    merchantPublicID: string,
    publicKey: string,
    keyVersion: number
  ) {
    return CloudpaymentsSdk.makeCardCryptogramPacket(
      cardNumber,
      expDate,
      cvv,
      merchantPublicID,
      publicKey,
      keyVersion
    );
  },
};
