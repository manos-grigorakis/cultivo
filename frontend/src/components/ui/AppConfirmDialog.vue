<script setup lang="ts">
import { LucideX } from '@lucide/vue'
import AppButton from '@/components/ui/AppButton.vue'

withDefaults(
  defineProps<{
    text?: string
    confirmButtonText?: string
    cancelButtonText?: string
    isSubmitting?: boolean
    isDangerOperation?: boolean
  }>(),

  {
    text: 'Are you sure you want to perform this action?',
    confirmButtonText: 'Yes',
    cancelButtonText: 'Cancel',
    isSubmitting: false,
    isDangerOperation: false,
  },
)

const emit = defineEmits<{
  close: []
  confirm: []
}>()
</script>

<template>
  <section
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    @click.self="emit('close')"
  >
    <div
      aria-modal="true"
      class="rounded-card bg-surface-muted relative w-full max-w-md p-6 shadow-xl"
      role="dialog"
    >
      <button
        aria-label="Close"
        class="hover:text-primary-700 hover:bg-surface absolute top-4 right-4 rounded-full p-1 hover:cursor-pointer"
        type="button"
        @click="emit('close')"
      >
        <LucideX :size="20" />
      </button>

      <h2 class="mt-6 px-6 text-center text-lg">{{ text }}</h2>

      <div class="mt-6 flex flex-wrap items-center justify-center gap-4">
        <AppButton
          :label="cancelButtonText"
          type="button"
          variant="outline"
          :disabled="isSubmitting"
          @on-click="emit('close')"
        />

        <AppButton
          :label="confirmButtonText"
          type="button"
          :variant="!isDangerOperation ? 'primary' : 'danger'"
          :disabled="isSubmitting"
          @on-click="emit('confirm')"
        />
      </div>
    </div>
  </section>
</template>

<style scoped></style>
