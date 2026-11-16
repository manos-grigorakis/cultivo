<script setup lang="ts">
import AppFormError from '@/components/ui/form/AppFormError.vue'
import AppFormLabel from '@/components/ui/form/AppFormLabel.vue'

const model = defineModel<string | null>()

withDefaults(
  defineProps<{
    label: string
    id: string
    placeholder?: string
    rows?: number
    required?: boolean
    disabled?: boolean
    errorMessage?: string
  }>(),
  {
    rows: 4,
    required: false,
    disabled: false,
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
      :disabled="disabled"
      :aria-invalid="!!errorMessage"
      :rows="rows"
      :placeholder="placeholder"
      :class="
        errorMessage ? 'border-red-500 focus:ring-red-500' : 'border-border focus:ring-primary-600'
      "
      class="rounded-card max-h-64 w-full resize-y overflow-auto border bg-white px-4 py-2 focus:ring-2 focus:outline-none"
    />

    <AppFormError :id="`${id}-error`" :message="errorMessage" />
  </div>
</template>

<style scoped></style>
