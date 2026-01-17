import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useUserStore } from '@/stores/user'
import { useConfigStore } from '@/stores/config'

const app = createApp(App)

app.use(createPinia())

// 初始化 stores
const userStore = useUserStore()
const configStore = useConfigStore()
userStore.init()
configStore.initTheme()

app.use(router)

app.mount('#app')
