module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true,
    'vue/setup-compiler-macros': true  // 添加这一行
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module'
  },
  globals: {
    defineProps: 'readonly',   // 添加
    defineEmits: 'readonly',   // 添加
    defineExpose: 'readonly',  // 添加
    defineOptions: 'readonly', // 添加
    defineSlots: 'readonly',   // 添加
    withDefaults: 'readonly' ,  // 添加
    skillAssociationRulesAPI: 'readonly',
    api: 'readonly',
    userBehaviorAPI: 'readonly'
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