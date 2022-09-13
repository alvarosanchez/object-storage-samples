package io.micronaut.examples.objectstorage.azure;

import com.azure.storage.blob.models.BlockBlobItem;
import io.micronaut.examples.objectstorage.UploadApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.objectstorage.azure.AzureBlobStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;

import java.util.Map;

/**
 * Azure-specific implementation of {@link UploadApi}.
 */
@Controller("/cloud")
public class AzureUploadController implements UploadApi {

    private final AzureBlobStorageOperations objectStorage;

    public AzureUploadController(AzureBlobStorageOperations objectStorage) {
        this.objectStorage = objectStorage;
    }

    @Override
    public HttpResponse<String> upload(CompletedFileUpload fileUpload) {
        UploadRequest objectStorageUpload = UploadRequest.fromCompletedFileUpload(fileUpload);
        UploadResponse<BlockBlobItem> response = objectStorage.upload(objectStorageUpload, builder -> {
            builder.setTags(Map.of("project", "object-storage-samples"));
        });

        return UploadApi.fromUploadResponse(response);
    }

}
