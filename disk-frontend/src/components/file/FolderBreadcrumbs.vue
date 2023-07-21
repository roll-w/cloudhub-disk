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
    <n-breadcrumb separator=">">
        <n-breadcrumb-item>
            <slot name="root"></slot>
            <span v-if="!$slots.root" :class="getClass()"
                  @click="$router.push({name: driveFilePage})">
                  文件
            </span>
        </n-breadcrumb-item>
        <n-breadcrumb-item
                v-for="folder in (folderInfo.parents || [])">
            <span :class="getClass()"
                  @click="handleFolderClick($event, folder)">
                {{ folder.name }}
            </span>
        </n-breadcrumb-item>
        <n-breadcrumb-item v-if="folderInfo.storageId !== 0">
            <span :class="getClass()">
                {{ folderInfo.name }}
            </span>
        </n-breadcrumb-item>
    </n-breadcrumb>
</template>

<script setup>
import {driveFilePage, driveFilePageFolder} from "@/router";
import {useRouter} from "vue-router";

const router = useRouter()

const props = defineProps({
    folderInfo: {
        type: Object,
        default: () => {
        }
    },
    onFolderClick: {
        type: Function,
        default: null
    },
    size: {
        type: String,
        default: 'normal'
    }
})

const getClass = () => {
    if (props.size === 'normal') {
        return 'folder-breadcrumb'
    }
    if (props.size === 'small') {
        return 'folder-breadcrumb-sm'
    }
    return 'folder-breadcrumb'
}

const handleFolderClick = (e, folder) => {
    console.log("folder click")
    if (props.onFolderClick) {
        props.onFolderClick(e, folder)
        return
    }
    router.push({
        name: driveFilePageFolder,
        params: {
            folder: folder.storageId
        }
    })

}

</script>

<style scoped>
.folder-breadcrumb {
    @apply text-xl;
}

.folder-breadcrumb-sm {
    @apply text-base;
}
</style>