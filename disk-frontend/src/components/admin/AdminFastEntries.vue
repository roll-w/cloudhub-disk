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
    <n-grid cols="3" x-gap="10" y-gap="10">
        <n-gi v-for="option in entries">
            <n-card :bordered="false" embedded>
                <div class="flex py-3 text-2xl items-center">
                    <div class="text-amber-500">
                        {{ option.name }}
                    </div>
                    <div class="flex flex-fill items-center h-12 justify-end justify-items-stretch">
                        <router-link v-if="option.route" :to="{name: option.route}"
                                     class="entry-card">
                            &gt;
                        </router-link>
                        <div v-else class="entry-card">
                            &gt;
                        </div>
                    </div>
                </div>
            </n-card>
        </n-gi>
    </n-grid>
</template>

<script setup>

const props = defineProps({
    entries: {
        type: Array,
        required: true
    }
})

</script>

<style scoped>
.entry-card {
    @apply select-none text-xl transition-all duration-150
    font-semibold font-sans
    cursor-pointer
    hover:text-4xl hover:text-amber-500
    text-neutral-400 ease-in;
}

</style>