const path = require('path');
const { getDefaultConfig } = require('@react-native/metro-config');

const root = path.resolve(__dirname, '..');

module.exports = (async () => {
  const { withMetroConfig } = await import('react-native-monorepo-config');

  const defaultConfig = await getDefaultConfig(__dirname);

  return withMetroConfig(defaultConfig, {
    root,
    dirname: __dirname,
  });
})();
