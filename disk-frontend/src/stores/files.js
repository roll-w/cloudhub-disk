/*
 * Copyright (C) 2023 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {defineStore} from "pinia";

export const useFileStore = defineStore('file', {
    state: () => ({
        uploads: [],
        showUploadDialog: false,
    }),
    getters: {
        getUploads: state => state.uploads,

        isUploadDialog: state => state.showUploadDialog,
    },
    actions: {
        /**
         * @param uploads {Array}
         */
        setUploads(uploads) {
            this.uploads = uploads
        },
        /**
         * @param upload {Object}
         */
        updateUpload(upload) {
            const index = this.uploads.findIndex(item => item.id === upload.id)
            if (index !== -1) {
                this.uploads[index] = upload
                return
            }
            this.uploads.push(upload)

        },
        removeUpload(upload) {
            const index = this.uploads.findIndex(item => item.id === upload.id)
            if (index !== -1) {
                this.uploads.splice(index, 1)
            }
        },
        findUpload(id) {
            return this.uploads.find(item => item.id === id)
        },

        showTransferDialog() {
            this.showUploadDialog = true
        },

        hideTransferDialog() {
            this.showUploadDialog = false
        }
    }
});

export class UploadFile {

}
