<script setup lang="ts">
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
  }>(),
  { required: false, type: 'text' },
)

const id = computed(() => props.inputId ?? generatedId)
</script>

<template>
  <div class="flex flex-col gap-1">
    <AppFormLabel :id="id" :label="label" :required="required" />

    <input
      :type="type"
      :id="id"
      v-model="model"
      :required="required"
      class="border-border rounded-card focus:ring-primary-600 w-full border bg-white px-4 py-2 focus:ring-2 focus:outline-none"
      :placeholder="placeholder"
    />
  </div>
</template>
