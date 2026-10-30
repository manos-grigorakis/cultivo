<script setup lang="ts">
import AppFormLabel from '@/components/ui/form/AppFormLabel.vue'

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
    <AppFormLabel :id="id" :label="label" :required="required" />

    <textarea
      v-model="model"
      :id="id"
      :required="required"
      :rows="rows"
      :placeholder="placeholder"
      class="border-border rounded-card focus:ring-primary-600 max-h-64 w-full resize-y overflow-auto border bg-white px-4 py-2 focus:ring-2 focus:outline-none"
    />
  </div>
</template>

<style scoped></style>
