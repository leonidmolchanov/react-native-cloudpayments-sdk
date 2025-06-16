import Foundation
import Cloudpayments

extension PaymentData {
    convenience init?(from input: [String: Any]) {
        guard
            let amount = input["amount"] as? String,
            let currency = input["currency"] as? String
        else {
            print("❌ Ошибка: нет обязательных параметров amount и currency")
            return nil
        }

        self.init()

        setAmount(amount)
        setCurrency(currency)

        if let payerDict = input["payer"] as? [String: Any] {
            let payer = PaymentDataPayer(
                firstName: payerDict["firstName"] as? String ?? "",
                lastName: payerDict["lastName"] as? String ?? "",
                middleName: payerDict["middleName"] as? String ?? "",
                birth: payerDict["birth"] as? String ?? "",
                address: payerDict["address"] as? String ?? "",
                street: payerDict["street"] as? String ?? "",
                city: payerDict["city"] as? String ?? "",
                country: payerDict["country"] as? String ?? "",
                phone: payerDict["phone"] as? String ?? "",
                postcode: payerDict["postcode"] as? String ?? ""
            )
            setPayer(payer)
        }
      

        if let receiptDict = input["receipt"] as? [String: Any],
           let itemsArray = receiptDict["items"] as? [[String: Any]] {
          let items = itemsArray.compactMap { (itemDict: [String: Any]) -> Receipt.Item? in
              guard
                  let label = itemDict["label"] as? String,
                  let price = itemDict["price"] as? Double,
                  let quantity = itemDict["quantity"] as? Double,
                  let amount = itemDict["amount"] as? Double,
                  let vat = itemDict["vat"] as? Int,
                  let method = itemDict["method"] as? Int,
                  let object = itemDict["object"] as? Int
              else {
                  return nil
              }

              return Receipt.Item(
                  label: label,
                  price: price,
                  quantity: quantity,
                  amount: amount,
                  vat: vat,
                  method: method,
                  object: object
              )
          }

            let amountsDict = receiptDict["amounts"] as? [String: Double] ?? [:]
            let amounts = Receipt.Amounts(
                electronic: amountsDict["electronic"] ?? 0,
                advancePayment: amountsDict["advancePayment"] ?? 0,
                credit: amountsDict["credit"] ?? 0,
                provision: amountsDict["provision"] ?? 0
            )

            let receipt = Receipt(
                items: items,
                taxationSystem: receiptDict["taxationSystem"] as? Int ?? 0,
                email: receiptDict["email"] as? String ?? "",
                phone: receiptDict["phone"] as? String ?? "",
                isBso: receiptDict["isBso"] as? Bool ?? false,
                amounts: amounts
            )

            setReceipt(receipt)
        }

              if let recurrentDict = input["recurrent"] as? [String: Any],
           let interval = recurrentDict["interval"] as? String,
           let period = recurrentDict["period"] as? Int,
           let amount = recurrentDict["amount"] as? Double {

            let recurrentReceipt: Receipt? = nil // Опционально, если нужно заполнить, добавь обработку

            let recurrent = Recurrent(
                interval: interval,
                period: period,
                customerReceipt: recurrentReceipt,
                amount: Int(amount)
            )

            setRecurrent(recurrent)
        }
   
        setCultureName(input["cultureName"] as? String ?? "RU-ru")
        setDescription(input["description"] as? String)
        setEmail(input["email"] as? String)
        setAccountId(input["accountId"] as? String)
        setJsonData(input["jsonData"] as? String ?? "")
    }
}
