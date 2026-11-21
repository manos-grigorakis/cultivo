<script setup lang="ts">
import AppFormError from '@/components/ui/form/AppFormError.vue'
import AppFormLabel from '@/components/ui/form/AppFormLabel.vue'
import { computed, useId } from 'vue'

const model = defineModel<string | number | null>()
const generatedId = useId()

const props = withDefaults(
  defineProps<{
    label: string
    type?: 'text' | 'password' | 'email' | 'date' | 'datetime-local' | 'time' | 'number'
    inputId?: string
    placeholder?: string
    required?: boolean
    disabled?: boolean
    errorMessage?: string
  }>(),
  { required: false, disabled: false, type: 'text' },
)

const id = computed(() => props.inputId ?? generatedId)
</script>

<template>
  <div class="flex flex-col gap-1">
    <AppFormLabel :id="id" :label="label" :required="required" />

    <input
      v-model="model"
      :id="id"
      :type="type"
      :placeholder="placeholder"
      :required
      :disabled
      :aria-invalid="!!errorMessage"
      :class="[
        'rounded-card w-full border px-4 py-2 transition-colors',
        'enabled:focus:ring-2 enabled:focus:outline-none',
        errorMessage
          ? 'border-red-500 enabled:focus:ring-red-500'
          : 'border-border enabled:focus:ring-primary-600',
        disabled ? 'text-content-muted cursor-not-allowed bg-gray-100 opacity-60' : 'bg-white',
      ]"
    />

    <AppFormError :message="errorMessage" />
  </div>
</template>
