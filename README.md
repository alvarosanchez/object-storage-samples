# object-storage-samples

Contains sample [Micronaut](https://micronaut.io) applications that use the [Object Storage API](https://micronaut-projects.github.io/micronaut-object-storage/latest/guide/).

Includes the following internal projects:

* `object-storage-sample-core`: simple Java project that contains the API interface, and a cloud-portable implementation.
* `object-storage-sample-tck`: utility project with an abstract Spock specification to test both the cloud-specific and cloud-portable controllers.
* `object-storage-sample-aws`: AWS implementation.
* `object-storage-sample-azure`: Azure implementation.
* `object-storage-sample-gcp`: Google Cloud implementation.
* `object-storage-sample-oracle-cloud`: Oracle Cloud implementation.