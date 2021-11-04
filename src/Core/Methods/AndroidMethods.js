import {Platform} from 'react-native';
import DeepWallKidsException from '../../Exceptions/DeepWallKidsException';
import ErrorCodes from '../../Enums/ErrorCodes';
import ProrationTypes from '../../Enums/ProrationTypes';
import UpgradePolicies from '../../Enums/UpgradePolicies';

export default class AndroidMethods {
  nativeDeepWall;

  constructor(nativeDeepWall) {
    this.nativeDeepWall = nativeDeepWall;
  }

  isAndroid() {
    return Platform.OS === 'android'
  }

  /**
   * @param productId string
   */
  consumeProduct(productId) {
    if (!this.isAndroid()) {
      return;
    }

    if (!productId) {
      throw new DeepWallKidsException(ErrorCodes.PRODUCT_ID_REQUIRED);
    }

    this.nativeDeepWall.consumeProduct(productId);
  }

  /**
   * @param prorationType
   * @param upgradePolicy
   */
  setProductUpgradePolicy(prorationType, upgradePolicy) {
    if (!this.isAndroid()) {
      return;
    }

    this.validateProrationType(prorationType);
    this.validateUpgradePolicy(upgradePolicy);

    this.nativeDeepWall.setProductUpgradePolicy(prorationType, upgradePolicy)
  }

  /**
   * @param prorationType
   * @param upgradePolicy
   */
  updateProductUpgradePolicy(prorationType, upgradePolicy) {
    if (!this.isAndroid()) {
      return;
    }

    this.validateProrationType(prorationType);
    this.validateUpgradePolicy(upgradePolicy);

    this.nativeDeepWall.updateProductUpgradePolicy(prorationType, upgradePolicy)
  }

  /**
   * Validation
   * @param prorationType
   */
  validateProrationType(prorationType) {
    if (!Object.values(ProrationTypes).includes(prorationType)) {
      throw new DeepWallKidsException(ErrorCodes.PRORATION_TYPE_NOT_VALID);
    }
  }

  /**
   * Validation
   * @param upgradePolicy
   */
  validateUpgradePolicy(upgradePolicy) {
    if (!Object.values(UpgradePolicies).includes(upgradePolicy)) {
      throw new DeepWallKidsException(ErrorCodes.UPGRADE_POLICY_TYPE_NOT_VALID);
    }
  }
}
