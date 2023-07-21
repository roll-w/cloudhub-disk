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
    <div :class="['fixed left-1/2 z-10 flex items-center -translate-x-1/2 ease-in-out transition-all duration-300 ',
         checkedList.length ? 'opacity-100 bottom-16' : 'opacity-0 -bottom-20']">
        <div :style="{
            boxShadow: boxShadow2
        }"
             class="py-2 bg-neutral-700 rounded-xl px-7">
            <n-space size="large">
                <n-button v-for="option in options"
                          :disabled="option.disabled"
                          @click="handleOptionSelect(option.key)"
                          :theme-overrides="{
                              colorQuaternaryHover: 'rgba(172, 172, 172, 0.20)',
                              colorQuaternaryPressed: 'rgba(172, 172, 172, 0.20)',
                          }"
                          circle
                          quaternary>
                    <n-tooltip placement="top" trigger="hover">
                        <template #trigger>
                            <div v-if="option.icon">
                                <n-icon :component="option.icon()" :size="24" color="#fff">
                                </n-icon>
                            </div>
                            <n-icon v-else :size="24" color="#fff">
                                {{ option.label }}
                            </n-icon>
                        </template>
                        <template #default>
                            {{ option.label }}
                        </template>
                    </n-tooltip>


                </n-button>
            </n-space>
        </div>
    </div>
</template>

<script setup>
import {useThemeVars} from "naive-ui";

const {boxShadow2} = useThemeVars().value


const props = defineProps({
    checkedList: {
        type: Array,
        required: true
    },
    options: {
        type: Array,
        default: []
    },
    onOptionSelect: {
        type: Function,
        default: (key) => {
        }
    }
})

const handleOptionSelect = (key) => {
    props.onOptionSelect(key)
}

</script>