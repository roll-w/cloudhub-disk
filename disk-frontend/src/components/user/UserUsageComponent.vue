<!--
  - Copyright (C) 2023 RollW
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -        http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<script setup>
import UserUsageLineChart from "@/components/charts/user/UserUsageLineChart.vue";
import {getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {useSiteStore} from "@/stores/site";
import {formatFileSize} from "@/util/format";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {useUserStore} from "@/stores/user";
import {userStatsPage} from "@/router";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userStore = useUserStore()
const siteStore = useSiteStore()

const userUsage = ref({
    used: 0,
    total: 0
})


const handleClick = () => {
    router.push({name: userStatsPage})
}

const USER_USAGE = "user_storage_used"

const requestUserUsage = () => {
    const config = createConfig()
    proxy.$axios.get(api.restrictByKey('user', userStore.user.id, USER_USAGE),
            config)
            .then((response) => {
                const usage = response.data
                userUsage.value = {
                    used: usage.value,
                    total: (usage.restrict < 0) ? Infinity : usage.restrict
                }
            })
            .catch((error) => {
                console.log(error)
                popUserErrorTemplate(notification, error, "获取用户数据失败")
            })

}

requestUserUsage()

</script>

<template>
    <div :class="['px-4 py-5 border rounded-xl cursor-pointer ' +
          'duration-300 ' +
          'hover:bg-opacity-30 ' +
          'transition-all', siteStore.isDark
          ? 'border-neutral-800 hover:bg-neutral-500'
          : 'border-neutral-200 hover:bg-neutral-300 ']"
         @click="handleClick">
        <div class="text-xs text-neutral-500">用量统计</div>
        <div class="flex pb-1 text-sm">
            <div class="flex-fill select-none">
                {{ formatFileSize(userUsage.used) }}
                /
                {{ formatFileSize(userUsage.total, "无限制") }}
            </div>
        </div>

        <UserUsageLineChart :percentage="(userUsage.used / userUsage.total) * 100"/>
    </div>
</template>

<style scoped>

</style>