package io.micronaut.examples.objectstorage.gcp;

import com.google.cloud.storage.Blob;
import io.micronaut.examples.objectstorage.UploadApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.objectstorage.googlecloud.GoogleCloudStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;

import java.util.Map;

/**
 * GCP-specific implementation of {@link UploadApi}.
 */
@Controller("/cloud")
public class GcpUploadController implements UploadApi {

    private final GoogleCloudStorageOperations objectStorage;

    public GcpUploadController(GoogleCloudStorageOperations objectStorage) {
        this.objectStorage = objectStorage;
    }

    @Override
    public HttpResponse<String> upload(CompletedFileUpload fileUpload) {
        UploadRequest objectStorageUpload = UploadRequest.fromCompletedFileUpload(fileUpload);
        UploadResponse<Blob> response = objectStorage.upload(objectStorageUpload, builder -> {
            builder.setMetadata(Map.of("project", "object-storage-samples"));
        });

        return UploadApi.fromUploadResponse(response);
    }

}
