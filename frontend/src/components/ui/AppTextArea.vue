<script setup lang="ts">
const model = defineModel<string | null>()

withDefaults(
  defineProps<{
    label: string
    id: string
    placeholder?: string
    rows?: number
    required?: boolean
  }>(),
  {
    rows: 4,
    required: false,
  },
)
</script>

<template>
  <div class="flex flex-col gap-1">
    <label :for="id" class="text-sm font-semibold capitalize">
      <span v-if="required" class="text-red-500">*</span>{{ label }}
    </label>

    <textarea
      v-model="model"
      :id="id"
      :required="required"
      :rows="rows"
      :placeholder="placeholder"
      class="border-border rounded-card focus:ring-primary-600 w-full border bg-white px-4 py-2 focus:ring-2 focus:outline-none"
    />
  </div>
</template>

<style scoped></style>
