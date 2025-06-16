import Foundation
import CardIO
import Cloudpayments

/**
 * CardIO сканер для CloudPayments SDK iOS
 * Реализует протокол PaymentCardScanner для интеграции с платежной формой
 */
@objc(CloudpaymentsCardIOService)
public class CloudpaymentsCardIOService: NSObject {

    // MARK: - Properties

    /// Callback для передачи результатов сканирования
    private var scannerCompletion: ((String?, UInt?, UInt?, String?) -> Void)?

    /// JavaScript конфигурация CardIO из React Native
    private let config: [String: Any]?

    // MARK: - Initialization

    /**
     * Инициализация сервиса CardIO
     * @param config Конфигурация из JavaScript (cardScannerConfig)
     */
    @objc public init(config: [String: Any]?) {
        self.config = config
        super.init()
    }

    /**
     * Фабричный метод для создания сканера из JavaScript конфигурации
     * @param config Словарь с настройками CardIO из React Native
     * @return Настроенный экземпляр CloudpaymentsCardIOService или nil
     */
    @objc public static func fromJSConfig(_ config: [String: Any]?) -> CloudpaymentsCardIOService? {
        return CloudpaymentsCardIOService(config: config)
    }
}

// MARK: - PaymentCardScanner Protocol

extension CloudpaymentsCardIOService: PaymentCardScanner {
  public func startScanner(completion: @escaping (String?, UInt?, UInt?, String?) -> Void) -> UIViewController? {
return nil
  }
  

    /**
     * Запуск сканера карт
     * @param completion Callback с результатами сканирования (номер, месяц, год, CVV)
     * @return UIViewController для презентации или nil при ошибке
     */
//    public func startScanner(completion: @escaping (String?, UInt?, UInt?, String?) -> Void) -> UIViewController? {
//        // Сохраняем callback для передачи результатов
//        self.scannerCompletion = completion
//
//        // Создаем CardIO контроллер
//      guard let scanController = CardIOPaymentViewController(paymentDelegate: self) else {
//          print("Ошибка инициализации CardIO")
//          return nil
//      }
//
//        // Базовые настройки
////      scanController?.collectExpiry = true
////      scanController?.collectCVV = false
////      scanController?.hideCardIOLogo = true
//
//        return scanController
//    }
}

// MARK: - CardIOPaymentViewControllerDelegate

extension CloudpaymentsCardIOService: CardIOPaymentViewControllerDelegate {

    /**
     * Пользователь отменил сканирование
     */
    public func userDidCancel(_ paymentViewController: CardIOPaymentViewController!) {
        paymentViewController.dismiss(animated: true, completion: nil)
    }

    /**
     * Пользователь успешно отсканировал карту
     * @param cardInfo Информация о карте
     * @param paymentViewController CardIO контроллер
     */
    public func userDidProvide(_ cardInfo: CardIOCreditCardInfo!, in paymentViewController: CardIOPaymentViewController!) {
        // Передаем данные карты через callback
        self.scannerCompletion?(
            cardInfo.cardNumber,
            cardInfo.expiryMonth,
            cardInfo.expiryYear,
            cardInfo.cvv
        )

        // Закрываем сканер
        paymentViewController.dismiss(animated: true, completion: nil)
    }
}
