import { fixupConfigRules } from '@eslint/compat';
import reactNativeConfig from '@react-native/eslint-config/flat';
import prettierConfig from 'eslint-config-prettier';
import prettier from 'eslint-plugin-prettier';
import { defineConfig } from 'eslint/config';

export default defineConfig([
  {
    // fixupConfigRules shims plugins still using removed ESLint 8 APIs
    // (e.g. eslint-plugin-ft-flow).
    extends: [fixupConfigRules(reactNativeConfig), prettierConfig],
    plugins: { prettier },
    rules: {
      'react/react-in-jsx-scope': 'off',
      'prettier/prettier': 'error',
    },
  },
  {
    ignores: ['node_modules/', 'lib/'],
  },
]);
