package io.micronaut.examples.objectstorage.azure

import com.azure.storage.blob.BlobServiceClient
import io.micronaut.context.env.Environment
import io.micronaut.examples.objectstorage.tck.AbstractUploadControllerSpec
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.AutoCleanup
import spock.lang.Shared

import static io.micronaut.objectstorage.azure.AzureBlobStorageConfiguration.PREFIX

@MicronautTest(environments = Environment.AZURE)
class AzureUploadControllerSpec extends AbstractUploadControllerSpec implements TestPropertyProvider {

    public static final String CONTAINER_NAME = "profile-pictures-container"
    public static final String OBJECT_STORAGE_NAME = 'default'

    @Shared
    @AutoCleanup
    GenericContainer azuriteContainer = new GenericContainer(DockerImageName.parse('mcr.microsoft.com/azure-storage/azurite:3.18.0'))
            .withExposedPorts(10000)

    @Inject
    @Shared
    BlobServiceClient blobServiceClient

    @Override
    Map<String, String> getProperties() {
        azuriteContainer.start()
        super.getProperties() + [
                (PREFIX + '.' + OBJECT_STORAGE_NAME + '.endpoint'): "http://127.0.0.1:${azuriteContainer.getMappedPort(10000)}/devstoreaccount1",
                'azure.credential.storage-shared-key.account-name': 'devstoreaccount1',
                'azure.credential.storage-shared-key.account-key' : 'Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw=='
        ]
    }

    void setup() {
        blobServiceClient.createBlobContainer(CONTAINER_NAME)
    }

    void cleanup() {
        blobServiceClient.getBlobContainerClient(CONTAINER_NAME).delete()
    }
}
