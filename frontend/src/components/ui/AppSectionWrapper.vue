<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <section class="w-full min-w-0 mx-auto max-w-7xl">
    <h1 class="text-4xl font-display text-primary-700">{{ title }}</h1>

    <slot />
  </section>
</template>
