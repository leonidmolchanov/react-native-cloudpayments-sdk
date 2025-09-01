import Foundation
import Cloudpayments

extension PaymentData {
    convenience init?(from input: [String: Any]) {
        guard
            let amount = input["amount"] as? String,
            let currency = input["currency"] as? String
        else {

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

        if let recurrentDict = input["recurrent"] as? [String: Any] {
            let interval = recurrentDict["interval"] as? String ?? "Month"
            let period   = recurrentDict["period"] as? Int ?? 1

            func parseAmount(_ any: Any?) -> Decimal? {
                switch any {
                case let n as NSNumber:
                    return Decimal(string: n.stringValue)
                case let d as Double:
                    return Decimal(d)
                case let s as String:
                    // заменим запятую на точку на всякий случай
                    return Decimal(string: s.replacingOccurrences(of: ",", with: "."))
                default:
                    return nil
                }
            }

            var amount: Decimal?

            // 1) recurrent.amount (если вдруг есть)
            amount = parseAmount(recurrentDict["amount"])

            // 2) customerReceipt.amounts.electronic
            if amount == nil,
               let receipt = recurrentDict["customerReceipt"] as? [String: Any],
               let amounts = receipt["amounts"] as? [String: Any] {
                amount = parseAmount(amounts["electronic"])
            }

            // 3) сумма по items (amount или price)
            if amount == nil,
               let receipt = recurrentDict["customerReceipt"] as? [String: Any],
               let items = receipt["items"] as? [[String: Any]] {
                amount = items.reduce(Decimal(0)) { acc, it in
                    let a = parseAmount(it["amount"]) ?? parseAmount(it["price"]) ?? 0
                    return acc + a
                }
            }

            guard let decimalAmount = amount else {
                print("❌ Не удалось распарсить сумму из recurrent")
                return
            }

            // ✅ Конвертация в Float без округлений
            let floatAmount = (decimalAmount as NSDecimalNumber).floatValue

            let recurrent = Recurrent(
                interval: interval,
                period: period,
                customerReceipt: nil,
                amount: floatAmount
            )

            setRecurrent(recurrent)
        }


        if let jsonData = input["jsonData"] {
           do {
               let data = try JSONSerialization.data(withJSONObject: jsonData, options: [])
                let jsonString = String(data: data, encoding: .utf8)
                 setJsonData(jsonString ?? "")
               } catch {
                        print("❌ Ошибка сериализации jsonData: \(error.localizedDescription)")
                        }
               } else {
               }
        setCultureName(input["cultureName"] as? String ?? "RU-ru")
        setDescription(input["description"] as? String)
        setEmail(input["email"] as? String)
        setAccountId(input["accountId"] as? String)
    }
}
