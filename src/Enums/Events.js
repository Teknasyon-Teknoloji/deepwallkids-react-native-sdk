/**
 * Events
 */
export default {
  // Common events
  PAYWALL_REQUESTED: 'deepWallKidsPaywallRequested',
  PAYWALL_RESPONSE_RECEIVED: 'deepWallKidsPaywallResponseReceived',
  PAYWALL_RESPONSE_FAILURE: 'deepWallKidsPaywallResponseFailure',
  PAYWALL_OPENED: 'deepWallKidsPaywallOpened',
  PAYWALL_NOT_OPENED: 'deepWallKidsPaywallNotOpened',
  PAYWALL_ACTION_SHOW_DISABLED: 'deepWallKidsPaywallActionShowDisabled',
  PAYWALL_CLOSED: 'deepWallKidsPaywallClosed',
  PAYWALL_EXTRA_DATA_RECEIVED: 'deepWallKidsPaywallExtraDataReceived',
  PAYWALL_PURCHASING_PRODUCT: 'deepWallKidsPaywallPurchasingProduct',
  PAYWALL_PURCHASE_SUCCESS: 'deepWallKidsPaywallPurchaseSuccess',
  PAYWALL_PURCHASE_FAILED: 'deepWallKidsPaywallPurchaseFailed',
  PAYWALL_RESTORE_SUCCESS: 'deepWallKidsPaywallRestoreSuccess',
  PAYWALL_RESTORE_FAILED: 'deepWallKidsPaywallRestoreFailed',

  // Android ONLY events
  PAYWALL_CONSUME_SUCCESS: 'deepWallKidsPaywallConsumeSuccess',
  PAYWALL_CONSUME_FAIL: 'deepWallKidsPaywallConsumeFailure',
};
