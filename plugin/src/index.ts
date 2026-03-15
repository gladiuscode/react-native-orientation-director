import { type ConfigPlugin } from 'expo/config-plugins';
import { withAndroidConfiguration } from './withAndroidConfiguration';
import { withIosConfiguration } from './withIosConfiguration';

const withYourLibrary: ConfigPlugin = (config) => {
  config = withAndroidConfiguration(config);

  config = withIosConfiguration(config);

  return config;
};

export default withYourLibrary;
