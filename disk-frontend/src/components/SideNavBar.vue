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
    <!--  TODO: collapse sidebar -->
    <n-layout-sider :collapsed-width="0"
                    :native-scrollbar="false"
                    bordered
                    position="absolute"
                    width="var(--sidebar-width)">
        <div>
            <n-menu :options="option" :value="chooseOn"/>
        </div>
        <div class="pt-4 mx-2 ">
            <UserUsageComponent />
        </div>

    </n-layout-sider>
</template>

<script setup>
import {useRouter} from "vue-router";
import {onBeforeMount, ref} from "vue";
import {useMessage} from "naive-ui";
import {convertsToNMenuOptions, findMenuOptionByKey, keyUser} from "@/views/menu";
import UserUsageLineChart from "@/components/charts/user/UserUsageLineChart.vue";
import {useSiteStore} from "@/stores/site";
import UserUsageComponent from "@/components/user/UserUsageComponent.vue";

const siteStore = useSiteStore()

const props = defineProps({
    optionKey: {
        type: String,
        default: keyUser
    }
})

const message = useMessage()

const router = useRouter()

onBeforeMount(() => {

})

let routerName = router.currentRoute.value.name
const chooseOn = ref()

const calcChooseOption = () => {
    const menus = findMenuOptionByKey(props.optionKey).menus
    const menu = findByKeyOrAlias(routerName, menus)
    if (menu) {
        return menu.key
    }
    return routerName
}

const findByKeyOrAlias = (name, menus) => {
    if (!menus) {
        return null
    }
    for (const menu of menus) {
        if (menu.key === name || (menu.alias || []).includes(name)) {
            return menu
        }
        if (!menu.children) {
            continue
        }
        const child = findByKeyOrAlias(name, menu.children)
        if (child) {
            return child
        }
    }
}


chooseOn.value = calcChooseOption()

router.afterEach(() => {
    routerName = router.currentRoute.value.name
    chooseOn.value = calcChooseOption()
})

const option = convertsToNMenuOptions(findMenuOptionByKey(props.optionKey).menus);
</script>

<style scoped>

</style>