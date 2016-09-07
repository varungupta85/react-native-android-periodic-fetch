import React from 'react-native';

const ReactNativeAndroidPeriodicFetch = React.NativeModules.ReactNativeAndroidPeriodicFetch;

export default {
  start: (delayInMsec) => {
    return ReactNativeAndroidPeriodicFetch.start(delayInMsec);
  },
  stop: (delayInMsec) => {
    return ReactNativeAndroidPeriodicFetch.stop(delayInMsec)
  }
};
