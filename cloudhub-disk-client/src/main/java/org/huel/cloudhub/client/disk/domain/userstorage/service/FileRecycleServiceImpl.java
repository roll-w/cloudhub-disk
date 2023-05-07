package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileRecycleService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileRecycleServiceImpl implements FileRecycleService {
    private final UserFileStorageRepository userFileStorageRepository;
    private final StorageActionService storageActionService;

    public FileRecycleServiceImpl(UserFileStorageRepository userFileStorageRepository,
                                  StorageActionService storageActionService) {
        this.userFileStorageRepository = userFileStorageRepository;
        this.storageActionService = storageActionService;
    }

    @Override
    public List<AttributedStorage> listRecycle(StorageOwner storageOwner) {
        return Collections.unmodifiableList(
                userFileStorageRepository.getDeletedByOwner(
                        storageOwner.getOwnerId(),
                        storageOwner.getOwnerType()
                )
        );
    }

    @Override
    public void revertRecycle(long storageId, StorageType storageType,
                              StorageOwner storageOwner) {
        if (storageType != StorageType.FILE) {
            return;
        }
        StorageAction storageAction =
                storageActionService.openStorageAction(storageId, storageType);
        storageAction.restore();
    }
}