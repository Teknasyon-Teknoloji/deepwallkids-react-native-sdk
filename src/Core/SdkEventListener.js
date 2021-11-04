import {NativeEventEmitter, NativeModules} from 'react-native';
import DeepWallKidsException from '../Exceptions/DeepWallKidsException';
import ErrorCodes from '../Enums/ErrorCodes';
import EventBus from './EventBus';

export default class SdkEventListener {
  constructor() {
    if (!NativeModules.RNDeepWallKidsEmitter) {
      throw new DeepWallKidsException(
        ErrorCodes.NATIVE_MODULE_EVENT_EMITTER_NOT_FOUND,
      );
    }
  }

  listenEvents() {
    const NativeEventBusEmitter = new NativeEventEmitter(
      NativeModules.RNDeepWallKidsEmitter,
    );

    NativeEventBusEmitter.addListener('DeepWallKidsEvent', (DeepWallKidsEvent) => {
      EventBus.getInstance().fireEvent(
        DeepWallKidsEvent.event,
        typeof DeepWallKidsEvent.data === 'undefined' ? null : DeepWallKidsEvent.data,
      );
    });
  }
}
