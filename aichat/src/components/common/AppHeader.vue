<template>
    <div class="header">
      <div class="logo-container">
        <img :src="logo" alt="Logo" class="logo" />
      </div>
      <div class="nav-container">
        <div class="nav-item" v-for="item in navItems" :key="item.name">
          {{ item.name }}
        </div>
        <el-avatar size="small" class="user-avatar" @click="goToLogin"></el-avatar>
      </div>
    </div>
  </template>
  
  <script>
  import { useRouter } from 'vue-router';
  export default {
    name: 'AppHeader',
    setup() {
    const router = useRouter();

    const goToLogin = () => {
      router.push('/login');
    };

    return {
      goToLogin
    };
  },
    data() {
      return {
        logo: require('@/assets/images/logo.png'),
        navItems: [
          { name: '首页' },
          { name: '功能' },
          { name: '价格' },
          { name: '关于我们' }
        ]
      }
    }
  }
  </script>