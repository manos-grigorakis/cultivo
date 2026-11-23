<script setup lang="ts">
import {
  ChevronDown,
  Ellipsis,
  GitBranch,
  Grid3x3Icon,
  LayoutGrid,
  Leaf,
  Package,
  SproutIcon,
} from '@lucide/vue'
import { type Component, ref } from 'vue'
import { useRoute } from 'vue-router'

interface NavigationLink {
  label: string
  url: string
  icon: Component
  children?: NavigationLink[]
}

const route = useRoute()
const expandedMenus = ref<string[]>([])
const isMoreOpen = ref<boolean>(false)

const links: NavigationLink[] = [
  { label: 'plants', url: '/plants', icon: Leaf },
  { label: 'categories', url: '/plant-categories', icon: LayoutGrid },
  { label: 'species', url: '/plant-species', icon: SproutIcon },
  { label: 'varieties', url: '/plant-varieties', icon: GitBranch },
  { label: 'seed packets', url: '/seed-packets', icon: Package },
  {
    label: 'seedbeds',
    url: '/seedbeds',
    icon: Grid3x3Icon,
    children: [{ label: 'sowings', url: '/seedbed-sowings', icon: SproutIcon }],
  },
]

const mobileLinks = links.slice(0, 4)
const moreLinks = links.slice(4)

const toggleMenu = (url: string) => {
  expandedMenus.value = expandedMenus.value.includes(url)
    ? expandedMenus.value.filter((item) => item !== url)
    : [...expandedMenus.value, url]
}

const isLinkActive = (link: NavigationLink) =>
  route.path === link.url ||
  route.path.startsWith(`${link.url}/`) ||
  link.children?.some((child) => route.path === child.url || route.path.startsWith(`${child.url}/`))

const isLinkExactActive = (link: NavigationLink) => route.path === link.url

const isMoreActive = () => moreLinks.some((link) => isLinkActive(link))
</script>

<template>
  <!-- Desktop -->
  <aside class="bg-surface-muted hidden min-h-dvh w-3xs p-4 md:block">
    <RouterLink to="/" class="font-display text-primary-800 text-2xl">Cultivo</RouterLink>

    <nav aria-label="Main navigation">
      <ul class="mt-6 flex flex-col gap-1">
        <li v-for="link in links" :key="link.url">
          <div
            class="rounded-card flex items-center transition-colors"
            :class="{
              'bg-primary-300 text-primary-900': isLinkExactActive(link),
              'bg-primary-200 text-primary-900': isLinkActive(link) && !isLinkExactActive(link),
              'text-primary-900 hover:bg-primary-200': !isLinkActive(link),
            }"
          >
            <RouterLink
              :to="link.url"
              class="flex flex-1 items-center gap-2 py-3 pl-4 font-medium capitalize"
            >
              <div class="flex items-center gap-2">
                <component :is="link.icon" :size="20" />
                {{ link.label }}
              </div>
            </RouterLink>

            <button
              v-if="link.children?.length"
              type="button"
              :aria-label="`Toggle ${link.label} submenu`"
              :aria-expanded="expandedMenus.includes(link.url)"
              class="rounded-card cursor-pointer p-3"
              @click="toggleMenu(link.url)"
            >
              <ChevronDown
                :size="20"
                class="transition-transform duration-200"
                :class="{ 'rotate-180': expandedMenus.includes(link.url) }"
              />
            </button>
          </div>

          <ul
            v-if="link.children && expandedMenus.includes(link.url)"
            class="mt-1 ml-4 flex flex-col gap-1"
          >
            <li v-for="child in link.children" :key="child.url">
              <RouterLink
                :to="child.url"
                active-class="bg-primary-300 text-primary-900"
                class="text-primary-900 rounded-card hover:bg-primary-200 flex items-center gap-2 py-3 pl-4 font-medium capitalize"
              >
                <component :is="child.icon" :size="18" />{{ child.label }}
              </RouterLink>
            </li>
          </ul>
        </li>
      </ul>
    </nav>
  </aside>

  <!-- Mobile -->
  <nav class="md:hidden" aria-label="Mobile navigation">
    <RouterLink to="/" class="font-display text-primary-800 p-4 text-2xl">Cultivo</RouterLink>

    <div
      v-if="isMoreOpen"
      class="bg-surface-muted rounded-card fixed right-2 bottom-20 left-2 z-50 p-3 shadow-lg"
    >
      <ul class="flex flex-col gap-1">
        <li v-for="link in moreLinks" :key="link.url">
          <RouterLink
            :to="link.url"
            class="text-primary-900 hover:bg-primary-200 rounded-card flex items-center gap-3 p-3 font-medium capitalize"
            :class="{
              'bg-primary-300': isLinkActive(link),
            }"
            @click="isMoreOpen = false"
          >
            <component :is="link.icon" :size="20" />
            {{ link.label }}
          </RouterLink>

          <ul v-if="link.children?.length" class="ml-5">
            <li v-for="child in link.children" :key="child.url">
              <RouterLink
                :to="child.url"
                active-class="bg-primary-300"
                class="text-primary-900 hover:bg-primary-200 rounded-card flex items-center gap-3 p-3 font-medium capitalize"
                @click="isMoreOpen = false"
              >
                <component :is="child.icon" :size="18" />
                {{ child.label }}
              </RouterLink>
            </li>
          </ul>
        </li>
      </ul>
    </div>

    <!-- Bottom   -->
    <div class="bg-surface-muted fixed inset-x-0 bottom-0 z-50 px-2 py-2">
      <ul class="flex items-center justify-around gap-1">
        <li v-for="link in mobileLinks" :key="link.url" class="flex-1">
          <RouterLink
            :to="link.url"
            active-class="bg-primary-300"
            class="text-primary-900 hover:bg-primary-200 rounded-card flex flex-col items-center gap-1 px-1 py-2 text-xs font-medium capitalize"
          >
            <component :is="link.icon" :size="21" />
            <span>{{ link.label }}</span>
          </RouterLink>
        </li>

        <li class="flex-1">
          <button
            type="button"
            class="text-primary-900 hover:bg-primary-200 rounded-card flex w-full cursor-pointer flex-col items-center gap-1 px-1 py-2 text-xs font-medium"
            :class="{ 'bg-primary-300': isMoreOpen || isMoreActive() }"
            :aria-expanded="isMoreOpen"
            aria-label="More navigation options"
            @click="isMoreOpen = !isMoreOpen"
          >
            <Ellipsis :size="21" />
            <span>More</span>
          </button>
        </li>
      </ul>
    </div>
  </nav>
</template>
