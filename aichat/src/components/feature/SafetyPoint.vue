<template>
    <div class="safety-point">
      <div class="safety-icon">
        <img :src="iconSrc" alt="Safety Icon" />
      </div>
      <div class="safety-text">
        <h4>{{ title }}</h4>
        <p>{{ description }}</p>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'SafetyPoint',
    props: {
      icon: {
        type: String,
        required: true
      },
      title: {
        type: String,
        required: true
      },
      description: {
        type: String,
        required: true
      }
    },
    computed: {
      iconSrc() {
        return require(`@/assets/icons/${this.icon}.svg`)
      }
    }
  }
  </script>