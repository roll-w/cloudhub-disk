/*
 * Copyright (C) 2023 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router, {login} from './router'

import './assets/main.css'
import {useUserStore} from "@/stores/user";
import {createAxios} from "@/request/axios_config";
import '@/util/string_ext'

import * as echarts from 'echarts'

const app = createApp(App)

app.config.globalProperties.$echarts = echarts

app.use(createPinia())
app.use(router)

const debug = import.meta.env.MODE === 'development'

if (!debug) {
    console.log = () => {
    }
}

const meta = document.createElement('meta')
meta.name = 'naive-ui-style'
document.head.appendChild(meta)

const onLoginExpired = () => {
    console.log('登录已过期，请重新登录')
    window.$message.error('登录已过期，请重新登录')
    const userStore = useUserStore()
    userStore.logout()

    router.push({
        name: login
    }).then((failure) => {
        console.log(failure)
    })
}

const axios = createAxios(onLoginExpired)
app.config.globalProperties.$axios = axios


app.mount('#app')
