<script setup lang="ts" generic="T extends string | number">
import AppFormError from '@/components/ui/form/AppFormError.vue'

withDefaults(
  defineProps<{
    label: string
    idPrefix: string
    options: { label: string; value: T }[]
    required?: boolean
    disabled?: boolean
    errorMessage?: string
  }>(),
  { required: false, disabled: false },
)

const model = defineModel<T[]>({ default: () => [] })
</script>

<template>
  <div>
    <label class="text-sm font-semibold capitalize">
      {{ label }}<span v-if="required" class="ml-1 text-red-500">*</span>
    </label>

    <div
      role="group"
      class="mt-1 flex flex-wrap gap-2 pb-2"
      :aria-invalid="!!errorMessage"
      :aria-labelledby="`${idPrefix}-label`"
    >
      <div v-for="option in options" :key="option.value">
        <input
          :id="`${idPrefix}-${option.value}`"
          v-model="model"
          type="checkbox"
          :value="option.value"
          :required="required && model.length === 0"
          :disabled="disabled"
          class="peer sr-only"
        />
        <label
          :for="`${idPrefix}-${option.value}`"
          :class="[
            errorMessage
              ? 'peer-focus-visible:ring-red-500'
              : 'peer-focus-visible:ring-primary-600',
            disabled ? 'cursor-not-allowed opacity-50' : 'peer-hover:bg-primary-600 cursor-pointer',
          ]"
          class="rounded-card peer-checked:bg-primary-600 inline-block bg-white px-4 py-2 transition-colors peer-checked:text-white peer-hover:text-white peer-focus-visible:ring-2"
          >{{ option.label }}</label
        >
      </div>
    </div>

    <AppFormError :id="`${idPrefix}-error`" :message="errorMessage" />
  </div>
</template>

<style scoped></style>
