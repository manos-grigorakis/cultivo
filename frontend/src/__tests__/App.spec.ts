import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import App from '../App.vue'

describe('App', () => {
  it('mounts renders properly', async () => {
    const router = createRouter({
      history: createMemoryHistory(),
      routes: [
        { path: '/', component: { template: '<div>Home</div>' } },
        { path: '/:pathMatch(.*)*', component: { template: '<div>Test</div>' } },
      ],
    })

    await router.push('/')
    await router.isReady()

    const wrapper = mount(App, { global: { plugins: [router] } })
    expect(wrapper.exists()).toBe(true)
  })
})
