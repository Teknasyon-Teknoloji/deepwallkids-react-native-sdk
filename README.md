# DeepWallKids (react native sdk)

* This package gives' wrapper methods for deepwallkids sdks. [iOS](https://github.com/Teknasyon-Teknoloji/deepwallkids-ios-sdk) - [Android](https://github.com/Teknasyon-Teknoloji/deepwall-android-sdk)

* Before implementing this package, you need to have **api_key** and list of **actions**.

* You can get api_key and actions from [DeepWall Dashboard](https://console.deepwall.com/)


---


## Getting started

`$ npm install deepwallkids-react-native-sdk --save`

**React Native 0.59 and below**

Run `$ react-native link deepwallkids-react-native-sdk` to link the library.


### Installation Notes
- **IOS**
  - Set ios version to 10.0 or higher in `ios/Podfile` like: `platform :ios, '10.0'`
  - Remove `flipper` from `ios/Podfile` if exists.
  - Run `$ cd ios && pod install`

- **ANDROID**
  - Set `minSdkVersion` to 21 or higher in `android/build.gradle`
  - Add `maven { url 'https://raw.githubusercontent.com/Teknasyon-Teknoloji/deepwall-android-sdk/master/' }` into `android/build.gradle` (Add into repositories under allprojects)
  - Make sure your min gradle version is "3.6.4" or higher in `android/build.gradle`. (Check troubleshooting section to see example)


---


## Usage

### Let's start

- On application start you need to initialize sdk with api key and environment.
```javascript
import DeepWallKids, { DeepWallKidsEnvironments } from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().initialize('{API_KEY}', DeepWallKidsEnvironments.PRODUCTION);
```

- Before requesting any paywall you need to set UserProperties (device uuid, country, language). [See all parameters](https://github.com/Teknasyon-Teknoloji/deepwallkids-ios-sdk#configuration)
```javascript
import DeepWallKids, { DeepWallKidsUserProperties } from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().setUserProperties(
  new DeepWallKidsUserProperties({
    uuid: 'UNIQUE_DEVICE_ID_HERE (UUID)',
    country: 'us',
    language: 'en-us',
  }),
);
```

- After setting userProperties, you are ready for requesting paywall with an action key. You can find action key in DeepWall dashboard.
```javascript
import DeepWallKids from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().requestPaywall('{ACTION_KEY}');

// You can send extra parameter if needed as below
DeepWallKids.getInstance().requestPaywall('{ACTION_KEY}', {'sliderIndex': 2, 'title': 'Deepwall'});
```

- You can also close paywall.
```javascript
import DeepWallKids from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().closePaywall();
```

- When any of userProperties is changed, you need to call updateUserProperties method. (For example if user changed application language)
```javascript
import DeepWallKids from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().updateUserProperties({
  language: 'fr-fr',
});
```

- You can validate receipts like below.
```javascript
import DeepWallKids, { DeepWallKidsValidateReceiptTypes } from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().validateReceipt(DeepWallKidsValidateReceiptTypes.RESTORE);
```


### Events

- There is also bunch of events triggering before and after DeepWall Actions. You may listen any event like below.
```javascript
import DeepWallKids, { DeepWallKidsEventBus, DeepWallKidsEvents } from 'deepwallkids-react-native-sdk';

DeepWallKidsEventBus.getInstance().addListener(DeepWallKidsEvents.PAYWALL_OPENED, function (data) {
  console.log(
    'DeepWallKidsEvents.PAYWALL_OPENED',
    data
  );
});
```

- For example, you may listen all events from sdk like below.
```javascript
import { DeepWallKidsEventBus, DeepWallKidsEvents } from 'deepwallkids-react-native-sdk';

Object.values(DeepWallKidsEvents).map((item) => {
  DeepWallKidsEventBus.getInstance().addListener(item, function (data) {
    console.log(item, data);
  });
});
```

- Adding and removing event listener example
```javascript
import { DeepWallKidsEventBus, DeepWallKidsEvents } from 'deepwallkids-react-native-sdk';

componentDidMount() {
  DeepWallKidsEventBus.getInstance().addListener(DeepWallKidsEvents.PAYWALL_OPENED, this.paywallOpenedListener = data => {
    // handle the event
  })
}

componentWillUnmount() {
  DeepWallKidsEventBus.getInstance().removeListener(this.paywallOpenedListener);
}
```


### iOS Only Methods

- Sending extra data to paywall while it's open.
```javascript
import DeepWallKids from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().sendExtraDataToPaywall({appName: "My awesome app"});
```


### Android Only Methods

- For consumable products, you need to mark the purchase as consumed for consumable product to be purchased again.
```javascript
import DeepWallKids from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().consumeProduct('consumable_product_id');
```

- Use `setProductUpgradePolicy` method to set the product upgrade policy for Google Play apps.
```javascript
import DeepWallKids, { DeepWallKidsProrationTypes, DeepWallKidsUpgradePolicies } from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().setProductUpgradePolicy(
  DeepWallKidsProrationTypes.IMMEDIATE_WITHOUT_PRORATION,
  DeepWallKidsUpgradePolicies.ENABLE_ALL_POLICIES
);
```
  
- Use `updateProductUpgradePolicy` method to update the product upgrade policy within the app workflow before requesting paywalls.
```javascript
import DeepWallKids, { DeepWallKidsProrationTypes, DeepWallKidsUpgradePolicies } from 'deepwallkids-react-native-sdk';

DeepWallKids.getInstance().updateProductUpgradePolicy(
  DeepWallKidsProrationTypes.IMMEDIATE_WITHOUT_PRORATION,
  DeepWallKidsUpgradePolicies.ENABLE_ALL_POLICIES
);
```


---


## Notes
- You may find complete list of _events_ in [Enums/Events.js](./src/Enums/Events.js) or [Native Sdk Page](https://github.com/Teknasyon-Teknoloji/deepwallkids-ios-sdk#event-handling)
- **UserProperties** are:
    - uuid
    - country
    - language
    - environmentStyle
