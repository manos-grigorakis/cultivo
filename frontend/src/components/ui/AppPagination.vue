<script setup lang="ts">
import type { Page } from '@/types/pagination.interface'
import { LucideArrowLeft, LucideArrowRight } from '@lucide/vue'
import { computed } from 'vue'

const props = defineProps<{
  data?: Page
}>()

const emit = defineEmits<{
  'page-changed': [page: number]
}>()

const nextPage = () => {
  if (props.data && props.data.number < props.data.totalPages - 1) {
    emit('page-changed', props.data.number + 1)
  }
}

const previousPage = () => {
  if (props.data && props.data.number > 0) {
    emit('page-changed', props.data.number - 1)
  }
}

const isFirstPage = computed(() => !props.data || props.data?.number === 0)
const isLastPage = computed(() => !props.data || props.data.number >= props.data.totalPages - 1)
</script>

<template>
  <nav
    v-if="data && data.totalPages > 1"
    aria-label="Pagination"
    class="mt-8 flex items-center justify-center"
  >
    <ul class="flex items-center gap-2">
      <li>
        <button
          type="button"
          aria-label="Previous page"
          @click="previousPage"
          :disabled="isFirstPage"
          class="rounded-card disabled:bg-surface-muted disabled:text-content-muted cursor-pointer p-2 transition-colors disabled:cursor-not-allowed"
          :class="!isFirstPage ? 'text-content hover:bg-primary-100 bg-white' : ''"
        >
          <LucideArrowLeft />
        </button>
      </li>

      <li v-for="page in data.totalPages" :key="page">
        <button
          type="button"
          @click="$emit('page-changed', page - 1)"
          :aria-label="`Go to page ${page}`"
          class="rounded-card cursor-pointer px-4 py-2 transition-colors"
          :class="
            page - 1 === data.number ? 'bg-primary-700 text-white' : 'hover:bg-primary-100 bg-white'
          "
        >
          {{ page }}
        </button>
      </li>

      <li>
        <button
          type="button"
          aria-label="Next page"
          @click="nextPage"
          :disabled="isLastPage"
          class="rounded-card disabled:bg-surface-muted disabled:text-content-muted cursor-pointer p-2 transition-colors disabled:cursor-not-allowed"
          :class="!isLastPage ? 'text-content hover:bg-primary-100 bg-white' : ''"
        >
          <LucideArrowRight />
        </button>
      </li>
    </ul>
  </nav>
</template>
