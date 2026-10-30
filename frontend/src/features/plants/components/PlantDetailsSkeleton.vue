<script setup lang="ts"></script>

<template>
  <div class="animate-pulse" aria-hidden="true">
    <!-- Plant image -->
    <div class="bg-surface-muted rounded-card h-50 w-50 max-w-150" />

    <!-- Plant name -->
    <div class="bg-surface-muted mt-4 h-10 w-64 max-w-full rounded" />

    <!-- Status and actions -->
    <div class="mt-2 flex flex-wrap items-center justify-between gap-4">
      <div class="bg-surface-muted rounded-card h-9 w-24" />

      <div class="flex items-center gap-2">
        <div class="bg-surface-muted rounded-card h-10 w-32" />
        <div class="bg-surface-muted rounded-card h-10 w-32" />
        <div class="bg-surface-muted rounded-card h-10 w-12" />
      </div>
    </div>

    <!-- Navigation tabs -->
    <div class="bg-primary-100 rounded-card mt-4 mb-6 flex gap-2 p-2">
      <div class="bg-surface-muted rounded-card h-14 w-32" />
      <div class="bg-surface-muted rounded-card h-14 w-32" />
    </div>

    <!-- Nested view placeholder -->
    <div class="flex flex-col gap-4">
      <div class="bg-surface-muted h-6 w-48 rounded" />
      <div class="bg-surface-muted rounded-card h-24 w-1/3" />
    </div>
  </div>
</template>

<style scoped></style>
