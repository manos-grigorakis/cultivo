<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <section class="mx-auto w-full max-w-7xl min-w-0">
    <h1 class="font-display text-primary-700 text-4xl">{{ title }}</h1>

    <slot />
  </section>
</template>
