<script setup lang="ts"></script>

<template>
  <div
    role="status"
    aria-label="Loading plant species"
    class="mx-auto w-full max-w-5xl animate-pulse"
  >
    <!-- Header -->
    <div class="flex items-start gap-6">
      <!-- Image -->
      <div class="bg-plant-placeholder rounded-card aspect-square w-32 shrink-0" />

      <!-- Species Info -->
      <div class="min-w-0 flex-1 space-y-3">
        <div class="h-9 w-48 max-w-full rounded bg-gray-200" />
        <div class="h-4 w-36 max-w-full rounded bg-gray-200" />
        <div class="h-4 w-24 max-w-full rounded bg-gray-200" />
      </div>

      <!-- Actions -->
      <div class="flex shrink-0 items-center gap-2">
        <div class="size-10 rounded-lg bg-gray-200" />
        <div class="size-10 rounded-lg bg-gray-200" />
      </div>
    </div>

    <!-- Growing Requirements -->
    <div class="border-content-muted/40 my-8 border-y py-6">
      <div class="h-6 w-48 rounded bg-gray-200" />

      <div class="mt-5 grid grid-cols-3 gap-4">
        <div v-for="i in 3" :key="i" class="flex flex-col items-center gap-2">
          <div class="size-12 rounded-full bg-gray-200" />
          <div class="h-4 w-20 max-w-full rounded bg-gray-200" />
          <div class="h-5 w-24 max-w-full rounded bg-gray-200" />
        </div>
      </div>
    </div>

    <!-- General Information -->
    <div>
      <div class="mb-5 h-7 w-52 rounded bg-gray-200" />

      <div class="space-y-6">
        <!-- Description -->
        <div class="space-y-2">
          <div class="h-4 w-24 rounded bg-gray-200" />
          <div class="h-4 w-full rounded bg-gray-200" />
          <div class="h-4 w-5/6 rounded bg-gray-200" />
          <div class="h-4 w-2/3 rounded bg-gray-200" />
        </div>

        <!-- Timestamps -->
        <div class="grid max-w-lg grid-cols-2 gap-6">
          <div v-for="i in 2" :key="i" class="space-y-2">
            <div class="h-4 w-24 max-w-full rounded bg-gray-200" />
            <div class="h-5 w-32 max-w-full rounded bg-gray-200" />
          </div>
        </div>
      </div>
    </div>

    <span class="sr-only">Loading...</span>
  </div>
</template>

<style scoped></style>
