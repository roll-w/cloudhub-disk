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
        <div>
            <n-tabs animated
                    v-model:value="activeTab"
                    label-size="large" type="line">
                <n-tab-pane display-directive="show:lazy"
                            tab="个人文件" name="files">
                   <UserFilesView :user-id="userId"
                                  :folder-id="folder" />
                </n-tab-pane>
                <n-tab-pane display-directive="show:lazy"
                            tab="分享" name="share">
                    <UserSharesView :user-id="userId"/>
                </n-tab-pane>
                <n-tab-pane display-directive="show:lazy"
                            tab="收藏夹" name="favorites">
                    <UserFavoritesView :user-id="userId"/>
                </n-tab-pane>
            </n-tabs>

        </div>


    </div>
</template>
<script setup>
import {computed, getCurrentInstance, ref} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import api from "@/request/api";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import FileComponentsView from "@/views/file/FileComponentsView.vue";
import {
    userPersonalPage,
    userPersonalPageFavorites,
    userPersonalPageShares,
    userPersonalPageWithFolder
} from "@/router";
import FolderBreadcrumbs from "@/components/file/FolderBreadcrumbs.vue";
import UserSharesView from "@/views/user/homeview/UserSharesView.vue";
import UserFavoritesView from "@/views/user/homeview/UserFavoritesView.vue";
import UserFilesView from "@/views/user/homeview/UserFilesView.vue";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const userId = router.currentRoute.value.params.id

const personalFiles = ref([])
const folder = router.currentRoute.value.params.folder || 0

const selectTab = computed(() => {
    switch (router.currentRoute.value.name) {
        case userPersonalPage:
        case userPersonalPageWithFolder:
            return 'files'
        case userPersonalPageFavorites:
            return 'favorites'
        case userPersonalPageShares:
            return 'shares'
    }
    return 'files'
})

const activeTab = ref(selectTab.value)

</script>

<style scoped>

</style>