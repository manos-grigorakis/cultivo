<script setup lang="ts"></script>

<template>
  <div class="animate-pulse" role="status" aria-label="Loading seedbed details">
    <!-- Header -->
    <div class="space-y-3">
      <div class="h-9 w-56 rounded bg-gray-200"></div>

      <div class="flex items-center gap-3">
        <div class="h-4 w-20 rounded bg-gray-200"></div>
        <div class="h-4 w-36 rounded bg-gray-200"></div>
        <div class="h-4 w-16 rounded bg-gray-200"></div>
      </div>
    </div>

    <!-- Size & Features -->
    <div class="grid grid-cols-1 gap-2 md:grid-cols-2">
      <!-- Size -->
      <div class="rounded-card mt-4 bg-white p-4">
        <div class="mb-3 h-6 w-16 rounded bg-gray-200"></div>

        <div class="grid grid-cols-2 gap-2 md:grid-cols-3">
          <div v-for="i in 6" :key="i" class="bg-accent-100 rounded-card space-y-2 px-6 py-4">
            <div class="h-6 w-12 rounded bg-gray-200"></div>
            <div class="h-4 w-16 rounded bg-gray-200"></div>
          </div>
        </div>
      </div>

      <!-- Features -->
      <div class="rounded-card mt-4 bg-white p-4">
        <div class="mb-3 h-6 w-24 rounded bg-gray-200"></div>

        <div class="grid grid-cols-1 gap-2">
          <div
            v-for="i in 3"
            :key="i"
            class="bg-accent-100 rounded-card flex items-center gap-3 p-4"
          >
            <div class="size-5 shrink-0 rounded bg-gray-200"></div>

            <div class="flex-1 space-y-2">
              <div class="h-4 w-32 rounded bg-gray-200"></div>
              <div class="h-3 w-20 rounded bg-gray-200"></div>
            </div>

            <div class="size-5 rounded-full bg-gray-200"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Slots -->
    <div class="rounded-card mt-4 bg-white p-4">
      <div class="mb-4 flex items-center justify-between">
        <div class="h-6 w-16 rounded bg-gray-200"></div>
        <div class="h-3 w-40 rounded bg-gray-200"></div>
      </div>

      <div class="rounded-card overflow-hidden">
        <div class="bg-primary-200 space-y-3 p-4">
          <div class="h-4 w-24 rounded bg-gray-200"></div>

          <div v-for="row in 4" :key="row" class="flex gap-4 overflow-hidden">
            <div
              v-for="col in 10"
              :key="col"
              class="size-12 shrink-0 rounded-full bg-gray-200"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Notes -->
    <div class="rounded-card mt-4 space-y-3 bg-white p-4">
      <div class="h-6 w-20 rounded bg-gray-200"></div>
      <div class="h-4 w-3/4 rounded bg-gray-200"></div>
    </div>

    <!-- Record -->
    <div class="rounded-card mt-4 space-y-3 bg-white p-4">
      <div class="mb-3 h-6 w-20 rounded bg-gray-200"></div>

      <div v-for="i in 3" :key="i" class="flex items-center justify-between">
        <div class="h-4 w-28 rounded bg-gray-200"></div>
        <div class="h-4 w-32 rounded bg-gray-200"></div>
      </div>
    </div>

    <span class="sr-only">Loading...</span>
  </div>
</template>

<style scoped></style>
