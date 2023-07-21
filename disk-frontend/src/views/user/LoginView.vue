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
    <div class="w-100 h-100 px-10 py-5">
        <div class="pt-16 m-auto justify-items-center" style="max-width: 768px;">
            <n-card>
                <LoginForm v-if="isLoginPage()"/>
                <RegisterForm v-else/>
            </n-card>
        </div>
    </div>
</template>

<script setup>
import RegisterForm from "@/components/user/login/RegisterForm.vue";
import LoginForm from "@/components/user/login/LoginForm.vue";
import {useRouter} from "vue-router";
import {useUserStore} from "@/stores/user";
import {driveFilePage, index, login} from "@/router";
import {getCurrentInstance} from "vue";
import Logo from "@/components/icon/Logo.vue";

const router = useRouter()
const userStore = useUserStore()

const checkLogin = () => {
    if (userStore.isLogin) {
        router.push({
            name: driveFilePage
        })
    }
}

checkLogin()

const isLoginPage = () => {
    return router.currentRoute.value.name === login
}

</script>

<style scoped>
.center {
    display: flex;
    padding-right: 16px;
    padding-left: 16px;
    flex-direction: column;
    height: calc(100vh - 64px);
    justify-content: center;
    position: relative;
}
</style>
