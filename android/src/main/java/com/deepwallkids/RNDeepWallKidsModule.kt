package com.deepwallkids

import android.os.Bundle
import com.facebook.react.bridge.*
import com.google.gson.Gson
import deepwall.core.DeepWall
import deepwall.core.DeepWall.initDeepWallWith
import deepwall.core.DeepWall.setUserProperties
import deepwall.core.models.*
import io.reactivex.functions.Consumer
import kotlinx.coroutines.*
import manager.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Field
open class RNDeepWallKidsModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  override fun getName(): String {
    return "RNDeepWallKids"
  }

  var deepWallEmitter = RNDeepWallKidsEmitter(reactContext)

  /*
   * Whether DeepWall has initialized before or not. This flag will
   * be used to prevent multiple initializations.
   */
  private var isDeepWallInitialized = false

  @ReactMethod
  fun initialize(apiKey: String?, environment: Int) {

    // If the library has initialized before, do not do anything.
    if (isDeepWallInitialized) {
      return
    }

    // Set the flag as true here to prevent double calls getting through.
    isDeepWallInitialized = true

    observeDeepWallEvents()
    val deepWallEnvironment = if (environment == 1) DeepWallEnvironment.SANDBOX else DeepWallEnvironment.PRODUCTION

    CoroutineScope(Dispatchers.IO).launch {

      if (reactContext.hasCurrentActivity()) {
        initDeepWallWith(
          currentActivity!!.application,
          currentActivity!!,
          apiKey!!,
          deepWallEnvironment
        )
      }
      else{
        withTimeoutOrNull(10000L) {
          while (!reactContext.hasCurrentActivity()) {
            delay(250)
          }
        }

        if(reactContext.hasCurrentActivity()) {
          initDeepWallWith(
            currentActivity!!.application,
            currentActivity!!,
            apiKey!!,
            deepWallEnvironment
          )
        } else {
          val map = WritableNativeMap()
          val modelData = convertJsonToMap(convertJson(""))
          map.putMap("data", modelData)
          map.putString("event", "deepWallKidsInitFailure")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
      }
    }
  }

  @ReactMethod
  fun setUserProperties(userProperties: ReadableMap) {
    val uuid = userProperties.getString("uuid")
    val country = userProperties.getString("country")
    val language = userProperties.getString("language")
    setUserProperties(uuid!!, country!!, language!!)
  }

  @ReactMethod
  fun requestPaywall(actionKey: String, extraData: ReadableMap? = null) {
    var hashMap: HashMap<String, Any> = HashMap()
    var bundle = Bundle()
    if (extraData != null) {
      hashMap = extraData.toHashMap()
      bundle.putSerializable("data", hashMap)
    }
    DeepWall.showPaywall(this.currentActivity!!, actionKey, bundle)
  }

  @ReactMethod
  fun updateUserProperties(country: String,
                           language: String,
                           environmentStyle: Int = 0) {

    val theme: DeepWallEnvironmentStyle = if (environmentStyle == 0) DeepWallEnvironmentStyle.LIGHT else DeepWallEnvironmentStyle.DARK
    DeepWall.updateUserProperties(country, language, theme)
  }

  @ReactMethod
  fun closePaywall() {
    DeepWall.closePaywall()
  }

  @ReactMethod
  fun consumeProduct(productId: String) {
    DeepWall.consumeProduct(productId)
  }

  @ReactMethod
  fun validateReceipt(validationType: Int) {
    val validation = when (validationType) {
      1 -> DeepWallReceiptValidationType.PURCHASE
      2 -> DeepWallReceiptValidationType.RESTORE
      3 -> DeepWallReceiptValidationType.AUTOMATIC
      else -> DeepWallReceiptValidationType.PURCHASE
    }
    DeepWall.validateReceipt(validation)
  }

  @ReactMethod
  fun setProductUpgradePolicy(prorationType: Int, upgradePolicy : Int) {
    val prorationValidationType = when (prorationType) {
      0 -> ProrationType.UNKNOWN_SUBSCRIPTION_UPGRADE_DOWNGRADE_POLICY
      1 -> ProrationType.IMMEDIATE_WITH_TIME_PRORATION
      2 -> ProrationType.IMMEDIATE_AND_CHARGE_PRORATED_PRICE
      3 -> ProrationType.IMMEDIATE_WITHOUT_PRORATION
      4 -> ProrationType.DEFERRED
      5 -> ProrationType.NONE
      else -> ProrationType.NONE
    }

    val upgradePolicyValidation = when (upgradePolicy){
      0 -> PurchaseUpgradePolicy.DISABLE_ALL_POLICIES
      1 -> PurchaseUpgradePolicy.ENABLE_ALL_POLICIES
      2 -> PurchaseUpgradePolicy.ENABLE_ONLY_UPGRADE
      3 -> PurchaseUpgradePolicy.ENABLE_ONLY_DOWNGRADE
      else -> PurchaseUpgradePolicy.DISABLE_ALL_POLICIES
    }

    DeepWall.setProductUpgradePolicy(
        prorationType = prorationValidationType,
        upgradePolicy = upgradePolicyValidation
    )

  }

  @ReactMethod
  fun updateProductUpgradePolicy(prorationType: Int, upgradePolicy : Int) {
    val prorationValidationType = when (prorationType) {
      0 -> ProrationType.UNKNOWN_SUBSCRIPTION_UPGRADE_DOWNGRADE_POLICY
      1 -> ProrationType.IMMEDIATE_WITH_TIME_PRORATION
      2 -> ProrationType.IMMEDIATE_AND_CHARGE_PRORATED_PRICE
      3 -> ProrationType.IMMEDIATE_WITHOUT_PRORATION
      4 -> ProrationType.DEFERRED
      5 -> ProrationType.NONE
      else -> ProrationType.NONE
    }

    val upgradePolicyValidation = when (upgradePolicy){
      0 -> PurchaseUpgradePolicy.DISABLE_ALL_POLICIES
      1 -> PurchaseUpgradePolicy.ENABLE_ALL_POLICIES
      2 -> PurchaseUpgradePolicy.ENABLE_ONLY_UPGRADE
      3 -> PurchaseUpgradePolicy.ENABLE_ONLY_DOWNGRADE
      else -> PurchaseUpgradePolicy.DISABLE_ALL_POLICIES
    }

    DeepWall.updateProductUpgradePolicy(
        prorationType = prorationValidationType,
        upgradePolicy = upgradePolicyValidation
    )
  }

  private fun observeDeepWallEvents() {
    EventBus.subscribe(Consumer {

      var map = WritableNativeMap()

      when (it.type) {
        DeepWallEvent.PAYWALL_OPENED.value -> {
          map = WritableNativeMap()
          val data = it.data as PaywallOpenedInfo
          val modelMap = convertJsonToMap(convertJson(data))
          map.putMap("data", modelMap)
          map.putString("event", "deepWallKidsPaywallOpened")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
        DeepWallEvent.DO_NOT_SHOW.value -> {
          map = WritableNativeMap()
          val data = it.data as PaywallActionShowDisabledInfo
          val modelMap = convertJsonToMap(convertJson(data))
          map.putMap("data", modelMap)
          map.putString("event", "deepWallKidsPaywallActionShowDisabled")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
        DeepWallEvent.CLOSED.value -> {
          map = WritableNativeMap()
          val data = it.data as PaywallClosedInfo
          val modelMap = convertJsonToMap(convertJson(data))
          map.putMap("data", modelMap)
          map.putString("event", "deepWallKidsPaywallClosed")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
        DeepWallEvent.PAYWALL_PURCHASING_PRODUCT.value -> {
          map = WritableNativeMap()
          val data = it.data as PaywallPurchasingProductInfo
          val modelMap = convertJsonToMap(convertJson(data))
          map.putMap("data", modelMap)
          map.putString("event", "deepWallKidsPaywallPurchasingProduct")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
        DeepWallEvent.PAYWALL_PURCHASE_FAILED.value -> {
          map = WritableNativeMap()
          val data = it.data as SubscriptionErrorResponse
          val modelMap = convertJsonToMap(convertJson(data))
          map.putMap("data", modelMap)
          map.putString("event", "deepWallKidsPaywallPurchaseFailed")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
        DeepWallEvent.PAYWALL_PURCHASE_SUCCESS.value -> {
          map = WritableNativeMap()
          val data = it.data as SubscriptionResponse
          val modelMap = convertJsonToMap(convertJson(data))
          map.putMap("data", modelMap)
          map.putString("event", "deepWallKidsPaywallPurchaseSuccess")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }

        DeepWallEvent.PAYWALL_RESPONSE_FAILURE.value -> {
          map = WritableNativeMap()
          val data = it.data as PaywallFailureResponse
          val modelData = convertJsonToMap(convertJson(data))
          map.putMap("data", modelData)
          map.putString("event", "deepWallKidsPaywallResponseFailure")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }

        DeepWallEvent.PAYWALL_RESTORE_SUCCESS.value -> {
          map = WritableNativeMap()
          map.putString("data", it.data.toString())
          map.putString("event", "deepWallKidsPaywallRestoreSuccess")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }

        DeepWallEvent.PAYWALL_RESTORE_FAILED.value -> {
          map = WritableNativeMap()
          map.putString("data", it.data.toString())
          map.putString("event", "deepWallKidsPaywallRestoreFailed")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
        DeepWallEvent.EXTRA_DATA.value -> {
          map = WritableNativeMap()
          val modelData = it.data?.let { it1 -> convertJson(it1) }?.let { it2 -> convertJsonToMap(it2) }
          map.putMap("data", modelData)
          map.putString("event", "deepWallKidsPaywallExtraDataReceived")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }

        DeepWallEvent.PAYWALL_REQUESTED.value -> {
          map = WritableNativeMap()
          map.putString("data", "")
          map.putString("event", "deepWallKidsPaywallRequested")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }

        DeepWallEvent.PAYWALL_RESPONSE_RECEIVED.value -> {
          map = WritableNativeMap()
          map.putString("data", "")
          map.putString("event", "deepWallKidsPaywallResponseReceived")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }

        DeepWallEvent.CONSUME_SUCCESS.value -> {
          map = WritableNativeMap()
          map.putString("data", "")
          map.putString("event", "deepWallKidsPaywallConsumeSuccess")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }

        DeepWallEvent.CONSUME_FAILURE.value -> {
          map = WritableNativeMap()
          map.putString("data", "")
          map.putString("event", "deepWallKidsPaywallConsumeFailure")
          deepWallEmitter.sendEvent(reactContext, "DeepWallKidsEvent", map)
        }
      }
    })
  }

  @Throws(JSONException::class)
  private fun convertJsonToMap(jsonObject: JSONObject): WritableNativeMap? {
    val map = WritableNativeMap()
    val iterator = jsonObject.keys()
    while (iterator.hasNext()) {
      val key = iterator.next()
      val value = jsonObject[key]
      if (value is JSONObject) {
        map.putMap(key, convertJsonToMap(value))
      } else if (value is JSONArray) {
        map.putArray(key, convertJsonToArray(value))
      } else if (value is Boolean) {
        map.putBoolean(key, (value))
      } else if (value is Int) {
        map.putInt(key, (value))
      } else if (value is Double) {
        map.putDouble(key, (value))
      } else if (value is String) {
        map.putString(key, value)
      } else {
        map.putString(key, value.toString())
      }
    }
    return map
  }

  @Throws(JSONException::class)
  private fun convertJson(model: Any): JSONObject {
    val gson = Gson()
    val jsonInString = gson.toJson(model)
    return JSONObject(jsonInString)
  }


  @Throws(JSONException::class)
  private fun convertJsonToArray(jsonArray: JSONArray): WritableArray? {
    val array: WritableArray = WritableNativeArray()
    for (i in 0 until jsonArray.length()) {
      when (val value = jsonArray[i]) {
        is JSONObject -> {
          array.pushMap(convertJsonToMap((value)))
        }
        is JSONArray -> {
          array.pushArray(convertJsonToArray(value))
        }
        is Boolean -> {
          array.pushBoolean((value))
        }
        is Int -> {
          array.pushInt((value))
        }
        is Double -> {
          array.pushDouble((value))
        }
        is String -> {
          array.pushString(value)
        }
        else -> {
          array.pushString(value.toString())
        }
      }
    }
    return array
  }
}
