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
    percentage: {
        type: Number,
        default: 0
    },
})


const colors = [
    {
      breakpoint: 90,
      color: '#F44336'
    },
    {
        breakpoint: 65,
        color: '#FF9800'
    },
    {
        breakpoint: 0,
        color: '#4CAF50'
    },
    {
      breakpoint: 200,
      color: '#d7d7d7'
    }
]


const getColor = (percentage) => {
    for (const color of colors) {
        if (percentage >= color.breakpoint) {
            return color.color
        }
    }
    return colors[colors.length - 1].color
}

</script>

<template>
    <div class="w-full h-full">
        <n-progress :border-radius="15"
                    :height="6"
                    :color="getColor(percentage)"
                    rail-color="#dedfe1"
                    :percentage="percentage"
                    :show-indicator="false"
                    :stroke-width="1"
                    type="line"/>
    </div>
</template>

<style scoped>

</style>