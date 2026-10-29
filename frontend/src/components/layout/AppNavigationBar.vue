<script setup lang="ts">
import { LayoutGrid, Leaf, SproutIcon } from '@lucide/vue'
import type { Component } from 'vue'

const links: { label: string; url: string; icon: Component }[] = [
  { label: 'plants', url: '/plants', icon: Leaf },
  { label: 'categories', url: '/plant-categories', icon: LayoutGrid },
  { label: 'species', url: '/plant-species', icon: SproutIcon },
]
</script>

<template>
  <!-- Desktop -->
  <aside class="bg-surface-muted hidden min-h-dvh w-3xs p-4 md:block">
    <RouterLink to="/" class="font-display text-primary-800 text-2xl">Cultivo</RouterLink>

    <nav aria-label="Main navigation">
      <ul class="mt-6 flex flex-col gap-1">
        <li v-for="link in links" :key="link.url">
          <RouterLink
            :to="link.url"
            active-class="bg-primary-300 text-primary-900"
            class="text-primary-900 rounded-card hover:bg-primary-200 flex items-center gap-2 py-3 pl-4 font-medium capitalize"
            ><component :is="link.icon" :size="20" />{{ link.label }}</RouterLink
          >
        </li>
      </ul>
    </nav>
  </aside>

  <!-- Mobile -->
  <nav class="md:hidden" aria-label="Mobile navigation">
    <RouterLink to="/" class="font-display text-primary-800 p-4 text-2xl">Cultivo</RouterLink>

    <div class="bg-surface-muted fixed bottom-0 z-50 w-full px-2 py-4">
      <ul class="flex items-center justify-center gap-1">
        <li v-for="link in links" :key="link.url">
          <RouterLink
            :to="link.url"
            active-class="bg-primary-300 text-primary-900"
            class="text-primary-900 rounded-card hover:bg-primary-200 flex items-center gap-2 px-8 py-3 font-medium capitalize"
            ><component :is="link.icon" :size="20" />{{ link.label }}</RouterLink
          >
        </li>
      </ul>
    </div>
  </nav>
</template>
