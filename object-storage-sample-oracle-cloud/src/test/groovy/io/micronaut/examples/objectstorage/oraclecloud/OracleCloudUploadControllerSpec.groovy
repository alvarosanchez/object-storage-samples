package io.micronaut.examples.objectstorage.oraclecloud

import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.ObjectStorageClient
import com.oracle.bmc.objectstorage.model.CreateBucketDetails
import com.oracle.bmc.objectstorage.requests.CreateBucketRequest
import com.oracle.bmc.objectstorage.requests.DeleteBucketRequest
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import io.micronaut.core.annotation.NonNull
import io.micronaut.examples.objectstorage.tck.AbstractUploadControllerSpec
import io.micronaut.objectstorage.oraclecloud.OracleCloudStorageConfiguration
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.AutoCleanup
import spock.lang.Shared

@MicronautTest(environments = Environment.ORACLE_CLOUD)
@Property(name = 'spec.name', value = SPEC_NAME)
class OracleCloudUploadControllerSpec extends AbstractUploadControllerSpec implements TestPropertyProvider {

    public static final String BUCKET_NAME = "profile-pictures-bucket"
    public static final String SPEC_NAME = 'OracleCloudUploadControllerSpec'

    @Shared
    @AutoCleanup
    GenericContainer ociEmulator = new GenericContainer(DockerImageName.parse('cameritelabs/oci-emulator'))
            .withExposedPorts(12000)

    public static String endpoint

    @Inject
    OracleCloudStorageConfiguration configuration

    @Inject
    ObjectStorage objectStorage

    @Override
    Map<String, String> getProperties() {
        ociEmulator.start()
        endpoint = 'http://127.0.0.1:' + ociEmulator.getMappedPort(12000)
        return [
                'oci.fingerprint'                                  : '50:a6:c1:a1:da:71:57:dc:87:ae:90:af:9c:38:99:67',
                'oci.private-key-file'                             : "file:${new File('src/test/resources/key.pem').absolutePath}",
                'oci.region'                                       : 'sa-saopaulo-1',
                'oci.tenant-id'                                    : 'ocid1.tenancy.oc1..testtenancy',
                'oci.user-id'                                      : 'ocid1.user.oc1..testuser'
        ]
    }

    @Singleton
    @Requires(property = 'spec.name', value = SPEC_NAME)
    static class ObjectStorageListener implements BeanCreatedEventListener<ObjectStorageClient> {

        @Override
        ObjectStorageClient onCreated(@NonNull BeanCreatedEvent<ObjectStorageClient> event) {
            ObjectStorageClient client = event.bean
            client.endpoint = endpoint
            return client
        }
    }

    void setup() {
        def builder = CreateBucketDetails.builder()
                .compartmentId(System.getenv('ORACLE_CLOUD_TEST_COMPARTMENT_ID'))
                .name(BUCKET_NAME)
        objectStorage.createBucket(CreateBucketRequest.builder()
                .namespaceName(configuration.getNamespace())
                .createBucketDetails(builder.build())
                .build())

    }

    void cleanup() {
        def listObjectsRequest = ListObjectsRequest.builder()
                .namespaceName(configuration.getNamespace())
                .bucketName(BUCKET_NAME)
                .build()
        objectStorage.listObjects(listObjectsRequest).listObjects.objects.each {
            def deleteObjectRequest = DeleteObjectRequest.builder()
                    .namespaceName(configuration.getNamespace())
                    .bucketName(BUCKET_NAME)
                    .objectName(it.name)
                    .build()
            objectStorage.deleteObject(deleteObjectRequest)
        }
        objectStorage.deleteBucket(DeleteBucketRequest.builder()
                .namespaceName(configuration.getNamespace())
                .bucketName(BUCKET_NAME)
                .build())
    }

}
