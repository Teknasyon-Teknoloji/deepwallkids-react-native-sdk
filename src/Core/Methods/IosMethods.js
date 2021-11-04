import {Platform} from 'react-native';

export default class IosMethods {
  nativeDeepWall;

  constructor(nativeDeepWall) {
    this.nativeDeepWall = nativeDeepWall;
  }

  isIos() {
    return Platform.OS === 'ios'
  }

  /**
   * hidePaywallLoadingIndicator
   */
  hidePaywallLoadingIndicator() {
    if (!this.isIos()) {
      return;
    }

    this.nativeDeepWall.hidePaywallLoadingIndicator();
  }

  /**
   * sendExtraDataToPaywall
   *
   * @param extraData
   */
  sendExtraDataToPaywall(extraData) {
    if (!this.isIos()) {
      return;
    }

    this.nativeDeepWall.sendExtraDataToPaywall(extraData);
  }
}
