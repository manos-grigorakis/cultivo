<script setup lang="ts"></script>

<template>
  <div class="animate-pulse">
    <div class="flex flex-wrap items-center justify-between gap-2">
      <!-- Title -->
      <div class="h-8 w-64 rounded-md bg-gray-200 dark:bg-gray-700"></div>

      <!-- Action buttons -->
      <div class="flex gap-2">
        <div class="h-9 w-9 rounded-md bg-gray-200 dark:bg-gray-700"></div>
        <div class="h-9 w-9 rounded-md bg-gray-200 dark:bg-gray-700"></div>
      </div>
    </div>

    <!-- Dates -->
    <div class="mt-3 h-3 w-56 rounded bg-gray-200 dark:bg-gray-700"></div>

    <!-- Description -->
    <div class="mt-4 space-y-2">
      <div class="h-4 w-full rounded bg-gray-200 dark:bg-gray-700"></div>
      <div class="h-4 w-4/5 rounded bg-gray-200 dark:bg-gray-700"></div>
      <div class="h-4 w-2/5 rounded bg-gray-200 dark:bg-gray-700"></div>
    </div>
  </div>
</template>

<style scoped></style>
