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
const props = defineProps({
    group: {
        type: Object,
        required: true
    }
})

const getSortedContainers = () => {
    return props.group.containers.sort((a, b) => {
        return a.serial - b.serial
    })
}

</script>

<template>
    <div class="py-2">
        <n-card>
            <div class="flex align-bottom py-2">
                <div class="text-xl">{{ group.containerId }}</div>
                <div class="pl-5">
                    <n-tag :bordered="false"
                           size="medium"
                           type="primary">
                        {{ group.source }}
                    </n-tag>
                </div>
            </div>
            <n-table class="my-3">
                <thead>
                <tr>
                    <th>序列号</th>
                    <th>单块大小</th>
                    <th>容量</th>
                    <th>已用块/总块</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="container in getSortedContainers()">
                    <td>{{ container.serial }}</td>
                    <td>{{ container.blockSize }} KB</td>
                    <td>{{ container.limitMbs }} MB</td>
                    <td>{{ container.usedBlocks }} / {{ container.limitBlocks }}</td>
                </tr>
                </tbody>
            </n-table>
        </n-card>
    </div>

</template>

<style scoped>

</style>