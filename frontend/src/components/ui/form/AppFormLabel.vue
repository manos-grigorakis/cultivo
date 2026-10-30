<script setup lang="ts">
withDefaults(
  defineProps<{
    label: string
    id: string
    required?: boolean
  }>(),
  { required: false },
)
</script>

<template>
  <label :for="id" class="text-sm font-semibold capitalize">
    {{ label }}<span v-if="required" class="ml-1 text-red-500">*</span>
  </label>
</template>

<style scoped></style>
