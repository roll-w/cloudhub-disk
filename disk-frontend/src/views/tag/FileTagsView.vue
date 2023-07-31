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
    <div class="p-5">
        <n-h1 class="py-2">
            文件标签
        </n-h1>
        <div class="flex">
            <div class="w-1/2">
                <n-tree
                        :data="tagTreeOption"
                        :node-props="nodeProps"
                        expand-on-click
                        key-field="label"
                />
            </div>

            <div class="w-full">
                <n-card class="w-full h-full">
                    <n-h2>
                        已选择标签
                    </n-h2>
                    <div class="flex flex-wrap min-h-[30px]">
                        <n-tag
                                v-for="tag in selectedTags"
                                :key="tag.key"
                                class="m-1"
                                round
                                type="primary"
                                size="large"
                                closable
                                @close="handleClose(tag)"
                        >
                            {{ tag.label }}
                        </n-tag>
                    </div>
                    <div class="pt-4">
                        <n-button
                                class="mt-2"
                                type="primary"
                                @click="handleConfirm"
                        >
                            打开相关文件
                        </n-button>
                    </div>

                </n-card>
            </div>
        </div>
    </div>

</template>

<script setup>
import {reactive} from "vue";
import {useRouter} from "vue-router";
import {driveFileSearchPage} from "@/router";
import {getCurrentInstance} from "vue";
import {useNotification, useMessage, useDialog} from "naive-ui";

const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const tagTreeOption = [
    {
        label: '文件类型',
        children: [
            {
                label: '文档',
            },
            {
                label: '图片',
            },
            {
                label: '视频',
            },
            {
                label: '音频',
            },
            {
                label: '文本'
            }
        ]
    },
]

const nodeProps = ({option}) => {
    return {
        onClick() {
            if (option.children) {
                return
            }
            if (selectedTags.find(tag => tag.label === option.label)) {
                message.error('标签已选择')
                return
            }
            const parent = findParent(option)
            if (parent) {
                const parentTag = selectedTags
                        .map(tag => {
                            return {
                                parent: findParent(tag),
                                tag: tag
                            }
                        })
                        .find(tag => tag.parent.label === parent.label)
                if (parentTag) {
                    handleClose(parentTag.tag)
                }
            }

            selectedTags.push(option)
        }
    }
}

const router = useRouter()

const selectedTags = reactive([])
const handleClose = (tag) => {
    selectedTags.splice(selectedTags.indexOf(tag), 1)
}

const findParent = (tag) => {
    for (let i = 0; i < tagTreeOption.length; i++) {
        const parent = tagTreeOption[i]
        if (parent.children) {
            for (let j = 0; j < parent.children.length; j++) {
                const child = parent.children[j]
                if (child.label === tag.label) {
                    return parent
                }
            }
        }
    }
    return null
}

const handleConfirm = () => {
    if (selectedTags.length === 0) {
        message.error('请选择标签')
        return
    }

    toSearchView()
}

const toSearchView = () => {
    const parentsWithTag = selectedTags.map(tag => {
        return {
            label: findParent(tag).label,
            value: tag.label
        }
    })
    const expression = parentsWithTag.map(tag => {
        return `${tag.label}：${tag.value}`
    }).join(' ')

    router.push({
        name: driveFileSearchPage,
        query: {
            keyword: encodeURI(expression)
        }
    })
}


</script>