<template>
  <div id="app">
    <Header/>
    <router-view/>
    <Footer/>
  </div>
</template>

<script>
import Header from '@/components/common/AppHeader.vue'
import Footer from '@/components/common/AppFooter.vue'

export default {
  name: 'App',
  components: {
    Header,
    Footer
  }
}
</script>