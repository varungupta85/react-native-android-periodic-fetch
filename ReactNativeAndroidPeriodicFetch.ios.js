import React from 'react-native';

const ReactNativeAndroidPeriodicFetch = React.NativeModules.ReactNativeAndroidPeriodicFetch;

export default {
  start: () => {
    console.error('ReactNativeAndroidPeriodicFetch API in not supported for iOS')
  },
  stop: () => {
    console.error('ReactNativeAndroidPeriodicFetch API in not supported for iOS')
  }
};
