<script setup lang="ts">
import type { Component } from 'vue'

defineProps<{
  icon?: Component
  label: string
}>()
</script>

<template>
  <button
    class="flex items-center gap-2 px-4 py-2 text-white bg-primary-800 hover:bg-primary-900 font-display hover:cursor-pointer rounded-card"
    @click="$emit('onClick')"
  >
    <component :is="icon" :size="18" /> {{ label }}
  </button>
</template>
