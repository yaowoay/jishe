<template>
    <div class="feature-card">
      <div class="feature-image">
        <el-image :src="image" fit="cover" />
      </div>
      <div class="feature-content">
        <h3>{{ title }}</h3>
        <p>{{ description }}</p>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'FeatureCard',
    props: {
      title: {
        type: String,
        required: true
      },
      description: {
        type: String,
        required: true
      },
      image: {
        type: String,
        required: true
      }
    }
  }
  </script>