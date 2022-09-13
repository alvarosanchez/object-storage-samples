package io.micronaut.examples.objectstorage.oraclecloud;

import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import io.micronaut.examples.objectstorage.UploadApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;

import java.util.Map;

/**
 * OCI-specific implementation of {@link UploadApi}.
 */
@Controller("/cloud")
public class OracleCloudUploadController implements UploadApi {

    private final OracleCloudStorageOperations objectStorage;

    public OracleCloudUploadController(OracleCloudStorageOperations objectStorage) {
        this.objectStorage = objectStorage;
    }

    @Override
    public HttpResponse<String> upload(CompletedFileUpload fileUpload) {
        UploadRequest objectStorageUpload = UploadRequest.fromCompletedFileUpload(fileUpload);
        UploadResponse<PutObjectResponse> response = objectStorage.upload(objectStorageUpload, builder -> {
            builder.opcMeta(Map.of("project", "object-storage-samples"));
        });

        return UploadApi.fromUploadResponse(response);
    }

}
