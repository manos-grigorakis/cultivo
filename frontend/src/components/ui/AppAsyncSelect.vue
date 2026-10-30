<script setup lang="ts" generic="T extends object">
import { nextTick, onBeforeUnmount, ref, shallowRef } from 'vue'
import VSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'

interface PageResult<U> {
  items: U[]
  hasNext: boolean
}

const props = withDefaults(
  defineProps<{
    label: string
    id: string
    optionLabel: keyof T & string
    fetchOptions: (page: number, term: string) => Promise<PageResult<T>>
    placeholder?: string
    required?: boolean
  }>(),
  { placeholder: 'Select an option', required: false },
)

const emit = defineEmits<{ error: [error: unknown] }>()
const selectedOption = defineModel<T | null>({ default: null })

const options = shallowRef<T[]>([])
const isLoading = ref<boolean>(false)
const currentPage = ref<number>(0)
const hasNextPage = ref<boolean>(false)
const searchTerm = ref<string>('')
const loadMoreElements = ref<HTMLElement | null>(null)

let searchTimer: ReturnType<typeof setTimeout>
let latestRequest = 0

const load = async (page: number) => {
  const requestId = ++latestRequest // Ignore outdated request
  isLoading.value = true

  try {
    const result = await props.fetchOptions(page, searchTerm.value)
    if (requestId !== latestRequest) return

    // Replace on new search, append on load more
    options.value = page === 0 ? result.items : [...options.value, ...result.items]
    currentPage.value = page
    hasNextPage.value = result.hasNext
  } catch (error) {
    if (requestId === latestRequest) emit('error', error)
  } finally {
    if (requestId === latestRequest) isLoading.value = false
  }
}

const onSearch = (term: string) => {
  searchTerm.value = term
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => load(0), 300) // Debouncer on search
}

const loadMore = () => {
  if (isLoading.value || !hasNextPage.value) return
  load(currentPage.value + 1)
}

// Infinite scroll watches the `Loading more options...` footer inside the dropdown
// When it becomes visible (user scroll to end) the next page is requested
const observer = new IntersectionObserver(([entry]) => {
  if (entry?.isIntersecting) loadMore()
})

const onOpen = async () => {
  if (options.value.length === 0) await load(0) // Lazy initial fetch

  await nextTick()
  if (loadMoreElements.value) observer.observe(loadMoreElements.value)
}

const onClose = () => observer.disconnect()

// Release resources to prevent memory leaks
onBeforeUnmount(() => {
  clearTimeout(searchTimer)
  observer.disconnect()
})
</script>

<template>
  <div class="flex flex-col gap-1">
    <label :for="id" class="text-sm font-semibold capitalize">
      <span v-if="required" class="text-red-500">*</span>{{ label }}
    </label>

    <VSelect
      v-model="selectedOption"
      :input-id="id"
      class="app-vselect"
      :options="options"
      :filterable="false"
      :loading="isLoading"
      :label="optionLabel"
      :placeholder="placeholder"
      @search="onSearch"
      @open="onOpen"
      @close="onClose"
    >
      <template #no-options>No options found</template>
      <template #list-footer>
        <li v-show="hasNextPage" ref="loadMoreElements" class="text-center">
          Loading more options...
        </li>
      </template>
    </VSelect>
  </div>
</template>

<style scoped>
.app-vselect {
  --vs-font-size: 1rem;
  --vs-line-height: 1.5;
  --vs-border-width: 1px;
  --vs-border-color: var(--color-border);
  --vs-border-radius: var(--radius-card);
  --vs-controls-color: var(--color-content-muted);
  --vs-selected-color: var(--color-content);
  --vs-search-input-color: var(--color-content);
  --vs-dropdown-option-padding: 0.5rem 1rem;
  --vs-dropdown-option--active-bg: var(--color-primary-600);
  --vs-dropdown-option--active-color: #fff;
}

.app-vselect :deep(.vs__dropdown-toggle) {
  background: #fff;
  padding: 0.5rem 0.75rem;
}

/* Same as focus:ring-2 focus:ring-primary-600 */
.app-vselect:focus-within :deep(.vs__dropdown-toggle) {
  box-shadow: 0 0 0 2px var(--color-primary-600);
}

.app-vselect :deep(.vs__search::placeholder) {
  color: var(--color-content-muted);
}

.app-vselect :deep(.vs__dropdown-menu) {
  margin-top: 4px;
  border-top-style: solid;
}
</style>
