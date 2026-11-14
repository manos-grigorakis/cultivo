<script setup lang="ts">
const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
</script>

<template>
  <div class="animate-pulse">
    <!-- Header -->
    <div class="flex flex-wrap justify-between gap-2">
      <div>
        <div class="bg-primary-200 mb-2 h-7 w-32 rounded-md"></div>

        <div class="flex items-center gap-2">
          <div class="bg-primary-200 h-7 w-20 rounded-full"></div>
          <div class="bg-surface-muted h-4 w-48 rounded-md"></div>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex items-center gap-2">
        <div class="bg-primary-200 h-9 w-12 rounded-full"></div>
        <div class="bg-primary-200 h-9 w-12 rounded-full"></div>
      </div>
    </div>

    <div class="grid grid-cols-1 gap-2 lg:grid-cols-2">
      <!-- Details -->
      <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
        <h3 class="text-primary-900 font-display mb-2 text-xl">Details</h3>

        <div class="flex flex-col space-y-2">
          <div v-for="i in 7" :key="i" class="flex items-center justify-between gap-2 py-0.5">
            <div class="bg-surface-muted h-4 w-20 rounded-md"></div>
            <div class="bg-primary-200 h-4 rounded-md" :class="i % 2 === 0 ? 'w-28' : 'w-36'"></div>
          </div>
        </div>
      </div>

      <!-- Growing Specs -->
      <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
        <h3 class="text-primary-900 font-display mb-2 text-xl">Growing Specs</h3>

        <div class="grid grid-cols-2 gap-2 md:grid-cols-3">
          <div
            v-for="i in 7"
            :key="i"
            class="bg-accent-100 rounded-card px-6 py-4"
            :class="i === 7 ? 'col-span-2' : ''"
          >
            <div class="bg-primary-200 mb-2 h-5 w-16 rounded-md"></div>
            <div class="bg-surface-muted h-3 w-24 max-w-full rounded-md"></div>
          </div>
        </div>
      </div>

      <!-- Sowing Calendar -->
      <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm lg:col-span-2">
        <h3 class="text-primary-900 font-display mb-2 text-xl">Sowing Calendar</h3>

        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm">
            <thead>
              <tr>
                <th class="p-1"></th>
                <th v-for="month in months" :key="month" class="p-1 font-medium">
                  {{ month }}
                </th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="row in ['Seedbed', 'Outdoor']" :key="row">
                <th scope="row" class="p-1">{{ row }}</th>
                <td v-for="i in 12" :key="i" class="p-1">
                  <div class="bg-content-muted/40 rounded-card h-6 w-8"></div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Month summaries -->
        <div class="mt-3 flex flex-wrap gap-x-6 gap-y-1">
          <div class="bg-surface-muted h-4 w-40 rounded-md"></div>
          <div class="bg-surface-muted h-4 w-36 rounded-md"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
