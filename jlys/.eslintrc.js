module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'vue/multi-word-component-names': 'off',
    'vue/no-unused-components': 'off',
    'space-before-function-paren': 'off',
    'no-unused-vars': 'warn',
    'no-trailing-spaces': 'off',
    semi: ['error', 'never'],
    quotes: ['error', 'single'],
    'comma-dangle': ['error', 'never'],
    'eol-last': 'off',
    indent: ['error', 2],
    'no-multiple-empty-lines': ['error', { max: 2 }]
  },
  ignorePatterns: [
    'src/components/vm-sdk/**/*',
    'src/components/interview/vm-sdk/**/*',
    '**/*.min.js',
    '**/node_modules/**'
  ]
}
