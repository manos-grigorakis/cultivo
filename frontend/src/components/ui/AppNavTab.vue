<script setup lang="ts">
defineProps<{
  routerName: string
  id: string | string[] | undefined | number
  label: string
  countBadge?: string | number
}>()
</script>

<template>
  <li>
    <RouterLink
      :to="{ name: routerName, params: { id: id } }"
      class="rounded-card relative block px-4 py-2 text-base font-medium capitalize lg:px-8 lg:py-4 lg:text-xl"
      :class="{ 'pr-9 lg:pr-12': countBadge != null }"
      active-class="bg-white shadow-sm"
      >{{ label }}
      <span
        v-if="countBadge"
        class="text-content-muted bg-primary-200 absolute top-1 right-1 flex min-h-5 min-w-5 items-center justify-center rounded-full px-1 text-[10px] leading-none lg:top-2 lg:right-2 lg:min-h-6 lg:min-w-6 lg:text-sm"
        >{{ countBadge }}</span
      >
    </RouterLink>
  </li>
</template>

<style scoped></style>
