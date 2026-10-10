<script setup lang="ts">
import type { Component } from 'vue'

type ButtonVariant = 'primary' | 'accent' | 'outline'

withDefaults(
  defineProps<{
    icon?: Component
    label: string
    disabled?: boolean
    type?: 'button' | 'submit'
    variant?: ButtonVariant
  }>(),
  { disabled: false, type: 'button', variant: 'primary' },
)

const variantClasses: Record<ButtonVariant, string> = {
  primary: 'bg-primary-800 text-white hover:bg-primary-900',
  accent: 'bg-accent-700 hover:bg-accent-800 text-white',
  outline: 'bg-transparent text-black border border-primary-700 hover:bg-primary-100 border-2',
}
</script>

<template>
  <button
    class="flex items-center gap-2 px-4 py-2 font-display rounded-card"
    :class="
      disabled ? 'cursor-not-allowed bg-content-muted' : ['cursor-pointer', variantClasses[variant]]
    "
    :disabled="disabled"
    :type="type"
    @click="$emit('onClick')"
  >
    <component :is="icon" :size="18" /> {{ label }}
  </button>
</template>
