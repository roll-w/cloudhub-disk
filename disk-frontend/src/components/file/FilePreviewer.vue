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
    <div>
        <div v-if="file.fileType === 'IMAGE'">
            <div class="frame">
                <n-image
                        :img-props="{class: 'h-[70vh] m-auto w-auto'}"
                        :src="urlUsed"
                        :theme-overrides="themeOverrides"
                        class="h-[70vh] m-auto w-full" object-fit="contain"/>
            </div>

        </div>
        <div v-else-if="file.fileType === 'TEXT'">
            <div class="frame border border-neutral-200 rounded-md">
                <n-scrollbar class="frame">
                    <n-code :code="text"
                            class="whitespace-pre-wrap "
                            show-line-numbers word-wrap/>
                </n-scrollbar>
            </div>

        </div>
        <div v-else-if="isPdf()">
            <embed :src="urlUsed" class="h-[70vh] w[75vw] m-auto" style="height: 75vh !important; width: 90vw !important;"
                   type="application/pdf"/>
        </div>
        <div v-else-if="file.fileType === 'AUDIO'">
            <audio :src="urlUsed" class="m-auto" controls/>
        </div>
        <div v-else-if="file.fileType === 'VIDEO'">
            <div class="frame rounded-md">
                <video :src="urlUsed" class="frame" controls></video>
            </div>

        </div>
        <div v-else>
            <div class="w-full h-full flex justify-center items-center">
                暂不支持预览该文件
            </div>
        </div>
    </div>

</template>

<script setup>
import api from "@/request/api";
import {useUserStore} from "@/stores/user";
import {computed, getCurrentInstance, ref} from "vue";
import {useThemeVars} from "naive-ui";

const {proxy} = getCurrentInstance()

const props = defineProps({
    file: {
        type: Object,
        required: true
    },
    url: {
        type: String,
        required: false
    }
})

const isPdf = () => {
    return props.file.name.toLowerCase().endsWith('.pdf')
}
const userStore = useUserStore()

const url = api.file(props.file.ownerType.toLowerCase(),
                props.file.ownerId, props.file.storageId) +
        "?token=" + userStore.getToken + (isPdf() ? "&disposition=inline" : "")

const urlUsed = computed(() => {
    if (props.url) {
        return props.url
    }
    return url
})

const text = ref('')
const requestText = async () => {
    if (props.file.fileType === 'TEXT') {
        await proxy.$axios.get(urlUsed).then(res => {
            text.value = res.data
        })
    }
}
requestText()


const {popoverColor, boxShadow2, textColor2, borderRadius} = useThemeVars().value;

const themeOverrides = {
    toolbarColor: popoverColor,
    toolbarBoxShadow: boxShadow2,
    toolbarIconColor: textColor2,
    toolbarBorderRadius: borderRadius,
};

</script>

<style scoped>
.frame {
    width: auto;
    margin: auto;
    height: 70vh;
}

</style>

<style>
.n-base-icon {
    width: auto !important;
}
</style>
