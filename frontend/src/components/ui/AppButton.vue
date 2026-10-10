<script setup lang="ts">
import type { Component } from 'vue'

withDefaults(
  defineProps<{
    icon?: Component
    label: string
    disabled?: boolean
    type?: 'button' | 'submit'
  }>(),
  { disabled: false, type: 'button' },
)
</script>

<template>
  <button
    class="flex items-center gap-2 px-4 py-2 text-white font-display rounded-card"
    :class="
      disabled
        ? 'cursor-not-allowed bg-content-muted'
        : 'cursor-pointer bg-primary-800 hover:bg-primary-900'
    "
    :disabled="disabled"
    :type="type"
    @click="$emit('onClick')"
  >
    <component :is="icon" :size="18" /> {{ label }}
  </button>
</template>
