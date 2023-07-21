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
        <div class="p-5">
            <n-card closable>
                <div class="text-4xl pb-5">
                    <Mix></Mix>
                </div>
            </n-card>
            <n-card closable>
                <div class="pb-5">
                    <RosePie></RosePie>
                </div>
            </n-card>
            <n-card closable>
                <div class="text-4xl pb-5">
                    <Bar></Bar>
                </div>
            </n-card>
            <n-card closable>
                <div class="text-4xl pb-5 w-full h-[70vh]">
                    <TagsTreeChart />
                </div>
            </n-card>
            <n-card closable>
                <div class="text-4xl pb-5">
                    <PolarHistogram></PolarHistogram>
                </div>
            </n-card>
        </div>
    </div>


</template>

<script setup>
import Mix from "@/components/echarts/mix.vue";
import RosePie from "@/components/echarts/RosePie.vue";
import Bar from "@/components/echarts/Bar.vue";
import Tree from "@/components/echarts/TagsTreeChart.vue";
import PolarHistogram from "@/components/echarts/PolarHistogram.vue";
import TagsTreeChart from "@/components/echarts/TagsTreeChart.vue";

</script>

<style scoped>

</style>