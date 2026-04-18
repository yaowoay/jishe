module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module'
  },
  globals: {
    defineProps: 'readonly',
    defineEmits: 'readonly',
    defineExpose: 'readonly',
    defineComponent: 'readonly',
    ref: 'readonly',
    reactive: 'readonly',
    computed: 'readonly',
    watch: 'readonly',
    onMounted: 'readonly',
    onUnmounted: 'readonly',
    onUpdated: 'readonly',
    onBeforeMount: 'readonly',
    onBeforeUpdate: 'readonly',
    onBeforeUnmount: 'readonly'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',

    'vue/multi-word-component-names': 'off',
    'vue/no-unused-components': 'off',

    'no-unused-vars': 'warn',
    'no-undef': 'warn',
    'space-before-function-paren': 'off',
    'no-trailing-spaces': 'off',
    'eol-last': 'off',

    semi: ['error', 'never'],
    quotes: ['error', 'single'],
    'comma-dangle': ['error', 'never'],
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