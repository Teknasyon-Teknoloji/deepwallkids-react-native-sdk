import React from 'react';
import {View, Button} from 'react-native';
import DeepWallKids, {
  DeepWallKidsEnvironments,
  DeepWallKidsUserProperties,
  DeepWallKidsEvents,
  DeepWallKidsEventBus,
} from 'deepwallkids-react-native-sdk';

const API_KEY = 'XXXX'; // Api key from deepwall console
const ACTION_KEY = 'AppLaunch'; // Action key from deepwall console

export default class App extends React.Component {
  eventListeners = [];

  constructor(props) {
    super(props);

    DeepWallKids.getInstance().initialize(API_KEY, DeepWallKidsEnvironments.PRODUCTION);
  }

  componentDidMount() {
    DeepWallKids.getInstance().setUserProperties(
      new DeepWallKidsUserProperties({
        uuid: 'deepwallkids-test-device-001',
        country: 'en',
        language: 'en-en',
      }),
    );

    // Listen and log all events
    Object.values(DeepWallKidsEvents).map(item => {
      DeepWallKidsEventBus.getInstance().addListener(item, this.eventListeners[item] = data => {
        console.log('Deepwallkids event received: ', item, data);
      });
    });
  }

  componentWillUnmount() {
    // Remove all listeners
    Object.values(DeepWallKidsEvents).map(item => {
      DeepWallKidsEventBus.getInstance().removeListener(this.eventListeners[item]);
    });
  }

  render() {
    return (
      <View style={Styles.wrapper}>
        <View style={Styles.buttonWrapper}>
          <Button
            title={'OPEN PAGE'}
            onPress={() => DeepWallKids.getInstance().requestPaywall(ACTION_KEY)}
          />
        </View>
      </View>
    );
  }
}

const Styles = {
  wrapper: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonWrapper: {
    width: 200,
  }
};
