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
import {computed} from "vue";
import {useSiteStore} from "@/stores/site";

const siteStore = useSiteStore()

const props = defineProps({
    name: {
        type: String,
        required: true
    },
    active: {
        type: Boolean,
        default: false
    },
})

const classes = computed(() => {
    if (props.active) {
        return "bg-amber-500 bg-opacity-[15%] "
    }
    if (siteStore.isDark) {
        return "bg-opacity-20 bg-neutral-700 hover:bg-neutral-300 hover:bg-opacity-20"
    }
    return "bg-opacity-25 bg-neutral-200 hover:bg-neutral-300 hover:bg-opacity-30"
})

</script>

<template>
    <div class="py-1">
        <div :class="['rounded-lg transition-all cursor-pointer ', classes]">
            <div class="flex items-end py-3 pl-3">
                <div :class="['text-lg',  active ? 'text-amber-500 ' : '']">{{ name }}</div>
            </div>
        </div>
    </div>

</template>

<style scoped>

</style>