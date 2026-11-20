<script setup lang="ts">
import AppFormError from '@/components/ui/form/AppFormError.vue'
import AppFormLabel from '@/components/ui/form/AppFormLabel.vue'
import { computed, useId } from 'vue'

const model = defineModel<boolean>({ default: false })
const generatedId = useId()

const props = withDefaults(
  defineProps<{
    label: string
    inputId?: string
    description?: string
    required?: boolean
    disabled?: boolean
    errorMessage?: string
  }>(),
  {
    required: false,
    disabled: false,
  },
)

const id = computed(() => props.inputId ?? generatedId)
</script>

<template>
  <div class="flex flex-col gap-1">
    <div
      class="rounded-card border-border flex items-center justify-between gap-4 border bg-white px-4 py-3"
    >
      <div class="flex flex-col gap-1">
        <AppFormLabel :id="id" :label="label" :required="required" />

        <p v-if="description" class="text-content-muted text-xs">
          {{ description }}
        </p>
      </div>

      <button
        :id="id"
        type="button"
        role="switch"
        :aria-checked="model"
        :aria-invalid="!!errorMessage"
        :aria-label="label"
        :disabled="disabled"
        :class="[
          'relative inline-flex h-6 w-11 shrink-0 cursor-pointer items-center rounded-full transition-colors duration-200',
          'focus-visible:ring-primary-600 focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-none',
          model ? 'bg-primary-600' : 'bg-gray-300',
          disabled && 'cursor-not-allowed opacity-50',
        ]"
        @click="model = !model"
      >
        <span
          :class="[
            'inline-block h-5 w-5 rounded-full bg-white shadow-sm transition-transform duration-200',
            model ? 'translate-x-5.5' : 'translate-x-0.5',
          ]"
        />
      </button>
    </div>

    <AppFormError :message="errorMessage" />
  </div>
</template>

<style scoped></style>
