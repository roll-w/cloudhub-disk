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

package tech.rollw.disk.web.domain.storage.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.cloudhub.client.CFSClient;
import org.cloudhub.client.CFSStatus;
import org.cloudhub.client.FileValidation;
import tech.rollw.disk.common.conf.ClientConfigLoader;
import tech.rollw.disk.web.domain.storage.DiskFileStorage;
import tech.rollw.disk.web.domain.storage.StorageService;
import tech.rollw.disk.web.domain.storage.dto.StorageAsSize;
import tech.rollw.disk.web.domain.storage.dto.CFSFile;
import tech.rollw.disk.web.domain.storage.repository.DiskFileStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class StorageServiceImpl implements StorageService {
    private final DiskFileStorageRepository diskFileStorageRepository;
    private final CFSClient cfsClient;
    private final ClientConfigLoader clientConfigLoader;

    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    public StorageServiceImpl(DiskFileStorageRepository diskFileStorageRepository,
                              CFSClient cfsClient,
                              ClientConfigLoader clientConfigLoader) {
        this.diskFileStorageRepository = diskFileStorageRepository;
        this.cfsClient = cfsClient;
        this.clientConfigLoader = clientConfigLoader;
    }

    @Override
    public CFSFile saveFile(InputStream inputStream) throws IOException {
        FileValidation fileValidation =
                cfsClient.uploadFile(inputStream, clientConfigLoader.getTempFilePath());
        IOUtils.closeQuietly(inputStream);
        long time = System.currentTimeMillis();
        DiskFileStorage exist = diskFileStorageRepository.getById(fileValidation.id());
        if (exist != null) {
            return new CFSFile(exist.getFileId(), exist.getFileSize());
        }
        DiskFileStorage diskFileStorage = new DiskFileStorage(
                fileValidation.id(),
                fileValidation.size(),
                time,
                time
        );
        diskFileStorageRepository.insert(diskFileStorage);

        return new CFSFile(diskFileStorage.getFileId(), diskFileStorage.getFileSize());
    }

    @Override
    public void getFile(String fileId, OutputStream outputStream) throws IOException {
        CFSStatus status = cfsClient.downloadFile(outputStream, fileId);
        if (!status.success()) {
            logger.debug("Download file error, fileId: {}, status: {}", fileId, status);
        }
    }

    @Override
    public void getFile(String fileId, OutputStream outputStream,
                        long startBytes, long endBytes) throws IOException {
        CFSStatus status =
                cfsClient.downloadFile(outputStream, fileId, startBytes, endBytes);
        if (!status.success()) {
            logger.debug("Download file error, fileId: {}, startBytes: {}, endBytes: {}, status: {}",
                    fileId, startBytes, endBytes, status);
        }
    }

    @Override
    public List<StorageAsSize> getFileSizes(List<String> fileIds) {
        return diskFileStorageRepository.getSizesByIds(fileIds);
    }

    @Override
    public long getFileSize(String fileId) {
        return diskFileStorageRepository.getSizeById(fileId);
    }

}
