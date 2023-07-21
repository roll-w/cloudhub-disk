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
        <n-form
                ref="createFolderForm"
                :model="createFolderFormValue"
                :rules="createFolderFormRules"
        >
            <n-form-item label="文件夹名称" path="name">
                <n-input v-model:value="createFolderFormValue.name" placeholder="输入文件夹名称" type="text"/>
            </n-form-item>
            <div class="pt-3">
                <n-button-group>
                    <n-button type="primary" @click="handleCreateFolderConfirm">
                        确认
                    </n-button>
                    <n-button secondary type="default" @click="onClickCancel()">取消</n-button>
                </n-button-group>
            </div>
        </n-form>
    </div>
</template>

<script setup>
import {getCurrentInstance, ref} from "vue";
import api from "@/request/api";
import {useMessage, useNotification} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import {popUserErrorTemplate} from "@/views/util/error";
import {useUserStore} from "@/stores/user";

const props = defineProps({
    onClickCancel: {
        type: Function,
        default: () => {
        }
    },
    onClickConfirm: {
        type: Function,
        default: () => {
        }
    },
    onBeforeAction: {
        type: Function,
        default: () => {
        }
    },
    onAfterAction: {
        type: Function,
        default: () => {
        }
    },
    folderId: {
        type: Number,
        default: 0
    },
    ownerType: {
        type: String,
        default: 'user'
    },
    ownerId: {
        type: Number,
        required: true
    }
})

const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()

const createFolderForm = ref()
const createFolderFormValue = ref({
    name: ''
})

const createFolderFormRules = ref({
    name: [
        {
            required: true,
            message: '请输入文件夹名称',
            trigger: 'blur'
        },
        {
            min: 1,
            max: 80,
            message: '长度在 1 到 80 个字符间',
            trigger: 'blur'
        }
    ]
})

const createFolder = (name) => {
    const config = createConfig()
    proxy.$axios.post(
        api.folder(props.ownerType, props.ownerId, props.folderId), {
            name: name
        },
        config
    ).then(res => {
        message.success('创建文件夹成功')
        props.onAfterAction()
    }).catch(err => {
        props.onAfterAction()
        popUserErrorTemplate(notification, err,
            '创建文件夹失败', '文件夹请求失败')
    })
}


const handleCreateFolderConfirm = () => {
    createFolderForm.value.validate().then(() => {
        props.onBeforeAction()
        props.onClickConfirm()
        createFolder(createFolderFormValue.value.name)
    })
}

</script>

<style scoped>

</style>