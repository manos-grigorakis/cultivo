<script setup lang="ts" generic="T extends string | number">
import AppFormLabel from '@/components/ui/form/AppFormLabel.vue'

interface SelectOption<T> {
  label: string
  value: T
}

const model = defineModel<T | null>({ required: true })

withDefaults(
  defineProps<{
    label: string
    id: string
    placeholder?: string
    options: SelectOption<T>[]
    required?: boolean
  }>(),
  { placeholder: 'Please select an option', required: false },
)
</script>

<template>
  <div class="flex flex-col gap-1">
    <AppFormLabel :id="id" :label="label" :required="required" />

    <select
      v-model="model"
      :id="id"
      :required="required"
      :class="model == null ? 'text-content-muted' : 'text-content'"
      class="border-border rounded-card focus:ring-primary-600 w-full border bg-white px-4 py-2 focus:ring-2 focus:outline-none"
    >
      <option :disabled="required" :value="null">{{ placeholder }}</option>
      <option v-for="option in options" :key="option.value" :value="option.value">
        {{ option.label }}
      </option>
    </select>
  </div>
</template>

<style scoped></style>
