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
// for optimizing page flashes in changing routes

import {getCurrentInstance, ref, watch} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import UserPageHeader from "@/components/user/personal/UserPageHeader.vue";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";
import SideLayoutTemplate from "@/views/SideLayoutTemplate.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userInfo = ref({})
const userId = ref(router.currentRoute.value.params.id)

const requestUserInfo = () => {
    const config = createConfig()
    proxy.$axios.get(api.userInfo(userId.value), config).then(resp => {
        userInfo.value = resp.data
    }).catch(error => {
        popUserErrorTemplate(notification, error)
    })
}

requestUserInfo()

watch(() => router.currentRoute.value.params.id, () => {
    userId.value = router.currentRoute.value.params.id
    requestUserInfo()
})

</script>

<template>
    <SideLayoutTemplate>
        <template #header>
            <div class="pt-5 px-5">
                <UserPageHeader :id="userInfo.userId"
                                :nickname="userInfo.nickname"
                                :role="userInfo.role"
                                :username="userInfo.username"/>
            </div>
        </template>
    </SideLayoutTemplate>
</template>

<style scoped>

</style>