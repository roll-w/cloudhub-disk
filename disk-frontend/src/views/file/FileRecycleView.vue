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

<template>
    <div class="p-5">
        <n-h1>
            回收站
        </n-h1>
        <div class="py-3 min-h-[30vh]">
            <FileComponentsView :file-options="fileOptions"
                                :disable-click="true"
                                :files="files">
                <template #before>
                    <div class="my-3">
                        <n-button-group>
                            <n-button secondary type="error" @click="">清空回收站</n-button>
                        </n-button-group>
                    </div>
                </template>

                <template #checkbox-tip>
                    <div class="text-neutral-500">
                        提示：回收站中的文件会在10天内自动清除，或者您可以手动清除。
                    </div>
                </template>
            </FileComponentsView>
        </div>
    </div>
</template>

<script setup>
import {ref, getCurrentInstance} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {popAdminErrorTemplate} from "@/views/util/error";
import {createConfig} from "@/request/axios_config";
import FileComponentsView from "@/views/file/FileComponentsView.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userStore = useUserStore()

const checkedState = ref([])

const files = ref([]);
const fileOptions = [
    {
        label: "还原",
        key: "revert",
    },
    {
        label: "删除",
        key: "delete",
    }
]

const requestRecycleBinFiles = () => {
    const config = createConfig()

    proxy.$axios.get(
            api.recycles('user', userStore.user.id),
            config).then(res => {
        files.value = res.data
        checkedState.value = new Array(files.value.length).fill(false)
    }).catch(err => {
        popAdminErrorTemplate(notification, err, "获取回收站文件失败")
    })
}

requestRecycleBinFiles()

</script>

<style scoped>

</style>