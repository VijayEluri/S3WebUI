package com.thoughtDocs.model.impl.s3;

/**
 * Created by IntelliJ IDEA.
 * User: ThoughtWorks
 * Date: Jul 29, 2009
 * Time: 9:42:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class RDocumentFixture extends DocumentFixture {

    @Override
    protected S3Bucket testBucket() {
        return BucketFactory.getTestS3Bucket();
    }
}
