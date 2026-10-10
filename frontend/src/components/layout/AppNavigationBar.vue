<script setup lang="ts">
import { Leaf } from '@lucide/vue'
import type { Component } from 'vue'

const links: { label: string; url: string; icon: Component }[] = [
  { label: 'plants', url: '/plants', icon: Leaf },
]
</script>

<template>
  <!-- Desktop -->
  <aside class="hidden p-4 bg-surface-muted w-3xs min-h-dvh md:block">
    <RouterLink to="/" class="text-2xl font-display text-primary-800">Cultivo</RouterLink>

    <nav aria-label="Main navigation">
      <ul class="flex flex-col gap-1 mt-6">
        <li v-for="link in links" :key="link.url">
          <RouterLink
            :to="link.url"
            active-class="bg-primary-300 text-primary-900"
            class="flex items-center gap-2 py-3 pl-4 font-medium capitalize text-primary-900 rounded-card hover:bg-primary-200"
            ><component :is="link.icon" :size="20" />{{ link.label }}</RouterLink
          >
        </li>
      </ul>
    </nav>
  </aside>

  <!-- Mobile -->
  <nav class="md:hidden" aria-label="Mobile navigation">
    <RouterLink to="/" class="p-4 text-2xl font-display text-primary-800">Cultivo</RouterLink>

    <div class="fixed bottom-0 z-50 w-full px-2 py-4 bg-surface-muted">
      <ul class="flex items-center justify-center gap-1">
        <li v-for="link in links" :key="link.url">
          <RouterLink
            :to="link.url"
            active-class="bg-primary-300 text-primary-900"
            class="flex items-center gap-2 px-8 py-3 font-medium capitalize text-primary-900 rounded-card hover:bg-primary-200"
            ><component :is="link.icon" :size="20" />{{ link.label }}</RouterLink
          >
        </li>
      </ul>
    </div>
  </nav>
</template>
