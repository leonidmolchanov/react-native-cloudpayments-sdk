/**
 * @fileoverview CloudPayments SDK Example App
 * @description Демонстрационное приложение для тестирования CloudPayments SDK
 * @author Leonid Molchanov
 * @since 1.0.0
 */

import React, { useState } from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { useCloudPayments } from '@lmapp/react-native-cloudpayments';
import type {
  IPaymentData,
  ReceiptItem,
  Amounts,
  Receipt,
  IPayer,
} from '@lmapp/react-native-cloudpayments';
import { KEY } from './key';
// ============================================================================
// CONFIGURATION
// ============================================================================

/**
 * ⚠️ ВАЖНО: Обязательно укажите ваш публичный ключ CloudPayments
 *
 * Получить ключ можно в личном кабинете CloudPayments:
 * https://merchant.cloudpayments.ru/
 *
 * Формат: pk_xxxxxxxxxxxxxxxxxxxxxxxx
 */
const PUBLIC_ID = KEY; // TODO: Замените на ваш ключ!

// ============================================================================
// TYPES
// ============================================================================

type TSection = 'payment-form' | 'card-payment' | 'apple-pay' | 'google-pay';

interface ISection {
  id: TSection;
  title: string;
  description: string;
  available: boolean;
}

// ============================================================================
// CONSTANTS
// ============================================================================

const SECTIONS: ISection[] = [
  {
    id: 'payment-form',
    title: 'Оплата через форму',
    description: 'Стандартная платежная форма CloudPayments',
    available: true,
  },
  {
    id: 'card-payment',
    title: 'Прямая оплата картой',
    description: 'Оплата с вводом данных карты в приложении',
    available: false, // Будет добавлено позже
  },
  {
    id: 'apple-pay',
    title: 'Apple Pay',
    description: 'Оплата через Apple Pay',
    available: false, // Будет добавлено позже
  },
  {
    id: 'google-pay',
    title: 'Google Pay',
    description: 'Оплата через Google Pay',
    available: false, // Будет добавлено позже
  },
];

// ============================================================================
// ТИПИЗИРОВАННЫЕ ДАННЫЕ
// ============================================================================

// Товары в чеке
const receiptItems: ReceiptItem[] = [
  {
    label: 'Премиум подписка',
    price: 999,
    quantity: 1,
    amount: 999,
    vat: null,
    method: 4, // Полная предварительная оплата до момента передачи предмета расчета
    object: 4, // Услуга
  },
  {
    label: 'Доставка',
    price: 1,
    quantity: 1,
    amount: 1,
    vat: null,
    method: 4,
    object: 4,
  },
];

// Суммы по способам оплаты
const amounts: Amounts = {
  electronic: 1000,
  advancePayment: 0,
  credit: 0,
  provision: 0,
};

// Информация о плательщике
const payer: IPayer = {
  firstName: 'Иван',
  lastName: 'Иванов',
  middleName: 'Иванович',
  birth: '1985-01-01',
  address: 'ул. Ленина, 10',
  street: 'ул. Ленина',
  city: 'Москва',
  country: 'RU',
  phone: '+79991234567',
  postcode: '101000',
};

// Чек онлайн-кассы
const receipt: Receipt = {
  items: receiptItems,
  taxationSystem: 2, // УСН доходы минус расходы
  isBso: false,
  amounts: amounts,
};

// Дополнительные данные (БЕЗ дублирования чека)
const jsonData = {
  SubscriptionId: 'com.df.twenty.diamonds',
  CustomerInfo: {
    age: 27,
    loyaltyLevel: 'premium',
  },
  OrderInfo: {
    source: 'mobile_app',
    campaign: 'summer_2024',
  },
};

// Основные данные платежа
const SAMPLE_PAYMENT_DATA: IPaymentData = {
  amount: '1000.00',
  currency: 'RUB',
  description: 'Тестовый платеж из Example App',
  email: 'test@example.com',
  accountId: 'user_12345',
  publicId: PUBLIC_ID,
  requireEmail: true,
  showResultScreen: true,
  payer: payer,
  receipt: receipt,
  jsonData: jsonData,
  enableCardScanner: true, // Включаем сканер карт для Android
  cardScannerConfig: {
    // Настройки полей карты
    requireExpiry: true,
    requireCVV: false,
    requirePostalCode: false,
    requireCardholderName: false,

    // Настройки интерфейса
    hideCardIOLogo: true,
    usePayPalLogo: false,
    suppressManualEntry: false,

    // Цветовая схема
    // actionBarColor: ECardIOColorScheme.MATERIAL_BLUE,
    // guideColor: ECardIOColorScheme.MATERIAL_GREEN,
    //
    // // Локализация
    // language: ECardIOLanguage.RUSSIAN,

    // Дополнительные настройки
    suppressConfirmation: false,
    suppressScan: false,
    keepApplicationTheme: false,
  },
};

// ============================================================================
// MAIN COMPONENT
// ============================================================================

export default function App(): React.JSX.Element {
  // ============================================================================
  // STATE
  // ============================================================================

  const [activeSection, setActiveSection] = useState<TSection>('payment-form');

  // ============================================================================
  // HOOKS
  // ============================================================================

  const [
    presentPaymentForm,
    { isLoading, isError, isSuccess, error, transactionId, status },
  ] = useCloudPayments(PUBLIC_ID, {
    onSuccess: (data) => {
      Alert.alert(
        '✅ Платеж успешен!',
        `ID транзакции: ${data.transactionId}`,
        [{ text: 'OK' }]
      );
    },
    onError: (data) => {
      Alert.alert('❌ Ошибка платежа', data.message, [{ text: 'OK' }]);
    },
    onCancel: () => {
      Alert.alert('⚠️ Платеж отменен', 'Пользователь отменил операцию', [
        { text: 'OK' },
      ]);
    },
  });

  // ============================================================================
  // HANDLERS
  // ============================================================================

  /**
   * Обработчик запуска платежной формы
   */
  const handlePaymentForm = async () => {
    try {
      const result = await presentPaymentForm(SAMPLE_PAYMENT_DATA);

      if (result.success) {
        console.log('Платеж успешен через Promise:', result.transactionId);
      } else {
        console.log('Платеж отклонен:', result.message);
      }
    } catch (err) {
      console.error('Ошибка при запуске платежа:', err);
    }
  };

  /**
   * Рендер секции с методом оплаты
   */
  const renderSection = (section: ISection) => {
    const isActive = activeSection === section.id;

    return (
      <TouchableOpacity
        key={section.id}
        style={[
          styles.sectionTab,
          isActive && styles.sectionTabActive,
          !section.available && styles.sectionTabDisabled,
        ]}
        onPress={() => section.available && setActiveSection(section.id)}
        disabled={!section.available}
      >
        <Text
          style={[
            styles.sectionTabText,
            isActive && styles.sectionTabTextActive,
            !section.available && styles.sectionTabTextDisabled,
          ]}
        >
          {section.title}
        </Text>
        {!section.available && <Text style={styles.comingSoonText}>Скоро</Text>}
      </TouchableOpacity>
    );
  };

  /**
   * Рендер контента активной секции
   */
  const renderSectionContent = () => {
    const section = SECTIONS.find((s) => s.id === activeSection);

    if (!section) return null;

    return (
      <View style={styles.sectionContent}>
        <Text style={styles.sectionTitle}>{section.title}</Text>
        <Text style={styles.sectionDescription}>{section.description}</Text>

        {activeSection === 'payment-form' && (
          <View style={styles.paymentFormSection}>
            <View style={styles.paymentDataCard}>
              <Text style={styles.paymentDataTitle}>
                Данные тестового платежа:
              </Text>
              <Text style={styles.paymentDataItem}>
                💰 Сумма: {SAMPLE_PAYMENT_DATA.amount}{' '}
                {SAMPLE_PAYMENT_DATA.currency}
              </Text>
              <Text style={styles.paymentDataItem}>
                📝 Описание: {SAMPLE_PAYMENT_DATA.description}
              </Text>
              <Text style={styles.paymentDataItem}>
                📧 Email: {SAMPLE_PAYMENT_DATA.email}
              </Text>
              <Text style={styles.paymentDataItem}>
                👤 ID пользователя: {SAMPLE_PAYMENT_DATA.accountId}
              </Text>
            </View>

            <TouchableOpacity
              style={[
                styles.paymentButton,
                isLoading && styles.paymentButtonDisabled,
              ]}
              onPress={handlePaymentForm}
              disabled={isLoading}
            >
              {isLoading ? (
                <View style={styles.loadingContainer}>
                  <ActivityIndicator color="#FFFFFF" size="small" />
                  <Text style={styles.paymentButtonText}>Обработка...</Text>
                </View>
              ) : (
                <Text style={styles.paymentButtonText}>
                  🚀 Запустить платежную форму
                </Text>
              )}
            </TouchableOpacity>

            {/* Статус платежа */}
            <View style={styles.statusContainer}>
              <Text style={styles.statusTitle}>Статус SDK:</Text>
              <View style={[styles.statusBadge, getStatusBadgeStyle(status)]}>
                <Text style={styles.statusText}>{getStatusText(status)}</Text>
              </View>

              {isError && error && (
                <View style={styles.errorContainer}>
                  <Text style={styles.errorTitle}>❌ Ошибка:</Text>
                  <Text style={styles.errorMessage}>{error.message}</Text>
                  {error.code && (
                    <Text style={styles.errorCode}>Код: {error.code}</Text>
                  )}
                </View>
              )}

              {isSuccess && transactionId && (
                <View style={styles.successContainer}>
                  <Text style={styles.successTitle}>✅ Успех!</Text>
                  <Text style={styles.successMessage}>
                    ID транзакции: {transactionId}
                  </Text>
                </View>
              )}
            </View>
          </View>
        )}
      </View>
    );
  };

  // ============================================================================
  // HELPERS
  // ============================================================================

  const getStatusText = (value: string) => {
    switch (value) {
      case 'idle':
        return 'Готов';
      case 'initializing':
        return 'Инициализация';
      case 'processing':
        return 'Обработка';
      case 'success':
        return 'Успех';
      case 'error':
        return 'Ошибка';
      case 'cancelled':
        return 'Отменено';
      default:
        return value;
    }
  };

  const getStatusBadgeStyle = (value: string) => {
    switch (value) {
      case 'success':
        return styles.statusSuccess;
      case 'error':
        return styles.statusError;
      case 'processing':
        return styles.statusProcessing;
      case 'cancelled':
        return styles.statusCancelled;
      default:
        return styles.statusIdle;
    }
  };

  // ============================================================================
  // RENDER
  // ============================================================================

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" backgroundColor="#FFFFFF" />

      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>CloudPayments SDK</Text>
        <Text style={styles.headerSubtitle}>Example App</Text>
      </View>

      {/* Warning about PUBLIC_ID */}
      {!PUBLIC_ID && (
        <View style={styles.warningContainer}>
          <Text style={styles.warningText}>
            ⚠️ Не забудьте заменить PUBLIC_ID на ваш ключ!
          </Text>
        </View>
      )}

      <ScrollView
        style={styles.scrollView}
        showsVerticalScrollIndicator={false}
      >
        {/* Section Tabs */}
        <View style={styles.sectionTabs}>{SECTIONS.map(renderSection)}</View>

        {/* Section Content */}
        {renderSectionContent()}
      </ScrollView>
    </SafeAreaView>
  );
}

// ============================================================================
// STYLES
// ============================================================================

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8F9FA',
  },
  header: {
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 20,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E9ECEF',
    alignItems: 'center',
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#212529',
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#6C757D',
    marginTop: 4,
  },
  warningContainer: {
    backgroundColor: '#FFF3CD',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#FFEAA7',
  },
  warningText: {
    color: '#856404',
    fontSize: 14,
    textAlign: 'center',
    fontWeight: '500',
  },
  scrollView: {
    flex: 1,
  },
  sectionTabs: {
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 16,
    paddingTop: 16,
    flexWrap: 'wrap',
    gap: 8,
  },
  sectionTab: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: 20,
    backgroundColor: '#F8F9FA',
    borderWidth: 1,
    borderColor: '#DEE2E6',
    minWidth: 120,
    alignItems: 'center',
  },
  sectionTabActive: {
    backgroundColor: '#007BFF',
    borderColor: '#007BFF',
  },
  sectionTabDisabled: {
    backgroundColor: '#F8F9FA',
    borderColor: '#DEE2E6',
    opacity: 0.6,
  },
  sectionTabText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#495057',
  },
  sectionTabTextActive: {
    color: '#FFFFFF',
  },
  sectionTabTextDisabled: {
    color: '#ADB5BD',
  },
  comingSoonText: {
    fontSize: 10,
    color: '#6C757D',
    marginTop: 2,
    fontStyle: 'italic',
  },
  sectionContent: {
    backgroundColor: '#FFFFFF',
    margin: 16,
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#212529',
    marginBottom: 8,
  },
  sectionDescription: {
    fontSize: 16,
    color: '#6C757D',
    marginBottom: 24,
    lineHeight: 24,
  },
  paymentFormSection: {
    gap: 20,
  },
  paymentDataCard: {
    backgroundColor: '#F8F9FA',
    borderRadius: 8,
    padding: 16,
    borderLeftWidth: 4,
    borderLeftColor: '#007BFF',
  },
  paymentDataTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#212529',
    marginBottom: 12,
  },
  paymentDataItem: {
    fontSize: 14,
    color: '#495057',
    marginBottom: 6,
    lineHeight: 20,
  },
  paymentButton: {
    backgroundColor: '#28A745',
    borderRadius: 8,
    paddingVertical: 16,
    paddingHorizontal: 24,
    alignItems: 'center',
    shadowColor: '#28A745',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 3,
  },
  paymentButtonDisabled: {
    backgroundColor: '#6C757D',
    shadowOpacity: 0,
    elevation: 0,
  },
  paymentButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
  loadingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  statusContainer: {
    gap: 12,
  },
  statusTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#212529',
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
    alignSelf: 'flex-start',
  },
  statusText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#FFFFFF',
  },
  statusIdle: {
    backgroundColor: '#6C757D',
  },
  statusProcessing: {
    backgroundColor: '#FFC107',
  },
  statusSuccess: {
    backgroundColor: '#28A745',
  },
  statusError: {
    backgroundColor: '#DC3545',
  },
  statusCancelled: {
    backgroundColor: '#6C757D',
  },
  errorContainer: {
    backgroundColor: '#F8D7DA',
    borderRadius: 8,
    padding: 12,
    borderLeftWidth: 4,
    borderLeftColor: '#DC3545',
  },
  errorTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#721C24',
    marginBottom: 4,
  },
  errorMessage: {
    fontSize: 14,
    color: '#721C24',
    lineHeight: 20,
  },
  errorCode: {
    fontSize: 12,
    color: '#721C24',
    marginTop: 4,
    fontFamily: 'monospace',
  },
  successContainer: {
    backgroundColor: '#D4EDDA',
    borderRadius: 8,
    padding: 12,
    borderLeftWidth: 4,
    borderLeftColor: '#28A745',
  },
  successTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#155724',
    marginBottom: 4,
  },
  successMessage: {
    fontSize: 14,
    color: '#155724',
    lineHeight: 20,
    fontFamily: 'monospace',
  },
});
