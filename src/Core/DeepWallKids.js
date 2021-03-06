import {NativeModules} from 'react-native';
import DeepWallKidsException from '../Exceptions/DeepWallKidsException';
import ErrorCodes from '../Enums/ErrorCodes';
import SdkEventListener from './SdkEventListener';
import CommonMethods from './Methods/CommonMethods';
import AndroidMethods from './Methods/AndroidMethods';
import IosMethods from './Methods/IosMethods';

export default class DeepWallKids {
  nativeDeepWall;
  listenSdkEvents = false;

  static getInstance() {
    if (!DeepWallKids.instance) {
      DeepWallKids.instance = new DeepWallKids();
    }

    return DeepWallKids.instance;
  }

  constructor() {
    this.nativeDeepWall = NativeModules.RNDeepWallKids;

    if (!this.nativeDeepWall) {
      throw new DeepWallKidsException(ErrorCodes.NATIVE_MODULE_NOT_FOUND);
    }

    // Enable SDK event listener only once (the way 'EventBus' works)
    if (!this.listenSdkEvents) {
      new SdkEventListener().listenEvents();
      this.listenSdkEvents = true;
    }
  }

  /**
   * @param apiKey
   * @param environment
   * @returns {DeepWallKids}
   */
  initialize(apiKey, environment) {
    new CommonMethods(this.nativeDeepWall).initialize(apiKey, environment);

    return this;
  }

  /**
   * @param {UserProperties} userProperties
   * @returns {DeepWallKids}
   */
  setUserProperties(userProperties) {
    new CommonMethods(this.nativeDeepWall).setUserProperties(userProperties);

    return this;
  }

  /**
   * @param country
   * @param language
   * @param environmentStyle
   * @param debugAdvertiseAttributions
   * @returns {DeepWallKids}
   */
  updateUserProperties({country, language, environmentStyle = 0, debugAdvertiseAttributions}) {
    new CommonMethods(this.nativeDeepWall).updateUserProperties({
      country,
      language,
      environmentStyle,
      debugAdvertiseAttributions,
    });

    return this;
  }

  requestPaywall(actionKey, extraData = null) {
    new CommonMethods(this.nativeDeepWall).requestPaywall(actionKey, extraData);
  }

  closePaywall() {
    new CommonMethods(this.nativeDeepWall).closePaywall();
  }

  validateReceipt(type) {
    new CommonMethods(this.nativeDeepWall).validateReceipt(type);
  }

  // iOS ONLY
  hidePaywallLoadingIndicator() {
    new IosMethods(this.nativeDeepWall).hidePaywallLoadingIndicator();
  }

  // iOS ONLY
  sendExtraDataToPaywall(extraData) {
    new IosMethods(this.nativeDeepWall).sendExtraDataToPaywall(extraData);
  }

  // Android ONLY
  consumeProduct(productId) {
    new AndroidMethods(this.nativeDeepWall).consumeProduct(productId);
  }

  // Android ONLY
  setProductUpgradePolicy(prorationType, upgradePolicy) {
    new AndroidMethods(this.nativeDeepWall).setProductUpgradePolicy(prorationType, upgradePolicy);
  }

  // Android ONLY
  updateProductUpgradePolicy(prorationType, upgradePolicy) {
    new AndroidMethods(this.nativeDeepWall).updateProductUpgradePolicy(prorationType, upgradePolicy);
  }
}
