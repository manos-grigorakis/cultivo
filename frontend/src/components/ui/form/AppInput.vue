<script setup lang="ts">
import AppFormError from '@/components/ui/form/AppFormError.vue'
import AppFormLabel from '@/components/ui/form/AppFormLabel.vue'
import { computed, useId } from 'vue'

const model = defineModel<string | number | null>()
const generatedId = useId()

const props = withDefaults(
  defineProps<{
    label: string
    type?: 'text' | 'password' | 'email' | 'date' | 'time' | 'number'
    inputId?: string
    placeholder?: string
    required?: boolean
    errorMessage?: string
  }>(),
  { required: false, type: 'text' },
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
      :aria-invalid="!!errorMessage"
      :class="[
        'rounded-card w-full border bg-white px-4 py-2 focus:ring-2 focus:outline-none',
        errorMessage ? 'border-red-500 focus:ring-red-500' : 'border-border focus:ring-primary-600',
      ]"
    />

    <AppFormError :message="errorMessage" />
  </div>
</template>
