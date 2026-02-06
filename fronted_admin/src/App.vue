<script setup>
import { NMessageProvider, NDialogProvider } from 'naive-ui'
</script>

<template>
  <n-message-provider>
    <n-dialog-provider>
      <router-view></router-view>
    </n-dialog-provider>
  </n-message-provider>
</template>

<style>
/* Global styles are handled by tailwind and style.css */
</style>
