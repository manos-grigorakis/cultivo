<script setup lang="ts"></script>

<template>
  <li class="rounded-card bg-white p-4 drop-shadow-sm" aria-hidden="true">
    <div class="flex h-full animate-pulse flex-col gap-4">
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div class="h-4.5 w-4.5 rounded bg-gray-200"></div>
        <div class="h-3 w-16 rounded bg-gray-200"></div>
      </div>

      <div class="space-y-2">
        <div class="h-5 w-3/4 rounded bg-gray-200"></div>
        <div class="h-4 w-1/2 rounded bg-gray-200"></div>
      </div>

      <div class="flex flex-wrap items-center gap-x-4 gap-y-2">
        <div class="flex items-center gap-x-1.5">
          <div class="h-4 w-4 rounded bg-gray-200"></div>
          <div class="h-4 w-14 rounded bg-gray-200"></div>
        </div>

        <div class="flex items-center gap-x-1.5">
          <div class="h-4 w-4 rounded bg-gray-200"></div>
          <div class="h-4 w-24 rounded bg-gray-200"></div>
        </div>
      </div>

      <div
        class="mt-auto flex flex-wrap items-center justify-between gap-x-4 gap-y-2 border-t border-gray-100 pt-3"
      >
        <div class="flex items-center gap-x-1.5">
          <div class="h-4 w-4 rounded bg-gray-200"></div>
          <div class="h-4 w-24 rounded bg-gray-200"></div>
        </div>

        <div class="flex items-center gap-x-1.5">
          <div class="h-4 w-4 rounded bg-gray-200"></div>
          <div class="h-4 w-16 rounded bg-gray-200"></div>
        </div>
      </div>
    </div>
  </li>
</template>

<style scoped></style>
