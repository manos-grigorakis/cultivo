<script setup lang="ts">
import { LucideX } from '@lucide/vue'
import { computed } from 'vue'

type ContainerSize = 'sm' | 'md' | 'lg' | 'xl' | '2xl'

const props = withDefaults(
  defineProps<{
    title: string
    containerSize?: ContainerSize
  }>(),
  { containerSize: 'md' },
)

const emit = defineEmits<{ close: [] }>()

const sizeClasses: Record<ContainerSize, string> = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-lg',
  xl: 'max-w-xl',
  '2xl': 'max-w-2xl',
}

const size = computed(() => sizeClasses[props.containerSize])
</script>

<template>
  <section
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    @click.self="emit('close')"
  >
    <div
      aria-modal="true"
      :class="size"
      class="rounded-card bg-surface-muted w-full p-6 shadow-xl"
      role="dialog"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="font-display text-primary-800 text-2xl">{{ title }}</h2>
          <span class="text-content-muted text-sm">Fields marked with * are required</span>
        </div>

        <button
          aria-label="Close"
          class="hover:text-primary-700 hover:bg-surface rounded-full p-1 hover:cursor-pointer"
          type="button"
          @click="emit('close')"
        >
          <LucideX :size="20" />
        </button>
      </div>

      <slot />
    </div>
  </section>
</template>

<style scoped></style>
