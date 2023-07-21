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
    <n-layout-footer bordered>
        <div class="p-10 leading-loose">
            <div class="text-sm leading-loose">
                <p>©2023 Cloudhub. All rights reserved. 版权所有</p>
                <a v-if="icp" href="https://beian.miit.gov.cn/" target="_blank">
                    {{ icp }}
                </a>
                <br>
                <a v-if="beian" href="https://www.beian.gov.cn/" target="_blank">
                    {{ beian }}
                </a>
            </div>
        </div>
    </n-layout-footer>
</template>

<script setup>

const icp = ((window.cloudhub || {}).server || {}).icp
const beian = ((window.cloudhub || {}).server || {}).beian

</script>

<style scoped>

</style>