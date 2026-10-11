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
    class="flex items-center justify-center mt-8"
  >
    <ul class="flex items-center gap-2">
      <li>
        <button
          type="button"
          aria-label="Previous page"
          @click="previousPage"
          :disabled="isFirstPage"
          class="p-2 transition-colors cursor-pointer rounded-card disabled:cursor-not-allowed disabled:bg-surface-muted disabled:text-content-muted"
          :class="!isFirstPage ? 'bg-white text-content hover:bg-primary-100' : ''"
        >
          <LucideArrowLeft />
        </button>
      </li>

      <li v-for="page in data.totalPages" :key="page">
        <button
          type="button"
          @click="$emit('page-changed', page - 1)"
          :aria-label="`Go to page ${page}`"
          class="px-4 py-2 transition-colors cursor-pointer rounded-card"
          :class="
            page - 1 === data.number ? 'bg-primary-700 text-white' : 'bg-white hover:bg-primary-100'
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
          class="p-2 transition-colors cursor-pointer rounded-card disabled:cursor-not-allowed disabled:bg-surface-muted disabled:text-content-muted"
          :class="!isLastPage ? 'bg-white text-content hover:bg-primary-100' : ''"
        >
          <LucideArrowRight />
        </button>
      </li>
    </ul>
  </nav>
</template>
