package com.thoughtDocs.model.impl.s3;

import com.amazon.s3.*;
import com.thoughtDocs.exception.InvalidBucketException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class S3BucketImpl implements S3Bucket {
    private AWSAuthConnection awsAuthConnection;
    private QueryStringAuthGenerator queryStringGenerator;
    private String name;

    public String getName() {
        return name;
    }

    public S3BucketImpl(String accessKeyId, String secretKey, String name) {
        this.name = name;
        awsAuthConnection = new AWSAuthConnection(accessKeyId, secretKey);
        queryStringGenerator = new QueryStringAuthGenerator(accessKeyId, secretKey);

    }

    public void ensureBucketOnServer() {
        try {
            if (!awsAuthConnection.checkBucketExists(this.name)) {
                awsAuthConnection.createBucket(name, AWSAuthConnection.LOCATION_DEFAULT, null).assertSuccess();
            }
        } catch (IOException e) {
            throw new InvalidBucketException(e);
        }
    }

    public String getAccessKeyId() {
        return awsAuthConnection.getAwsAccessKeyId();
    }

    public String getSecretAccessKey() {
        return awsAuthConnection.getAwsSecretAccessKey();
    }


    public boolean canConnect() {
        try {
            return awsAuthConnection.listAllMyBuckets(null).getEntries() != null;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean exists() {
        try {
            return awsAuthConnection.checkBucketExists(name);
        } catch (IOException e) {
            return false;
        }
    }

    public void saveObject(S3Object obj) throws IOException {
        obj.setBucket(this);
        com.amazon.s3.S3Object object = new com.amazon.s3.S3Object(obj.getData(), obj.getMeta());
        Response response = awsAuthConnection.put(name, obj.getKey(), object, null);
        response.assertSuccess();
    }
    public void updateObjectMeta(S3Object obj) throws IOException {
        awsAuthConnection.copy(name, obj.getKey(), name, obj.getKey(),obj.getMeta(), null);
    }

    public void removeObject(S3Object obj) throws IOException {
        Response response = awsAuthConnection.delete(name, obj.getKey(), null);
        response.assertSuccess();
    }

    public List<S3Object> getObjects() throws IOException {
        List<S3Object> retVal = new ArrayList<S3Object>();
        ListBucketResponse response = awsAuthConnection.listBucket(name, null, null, null, null);
        if (response != null) {
            for (Object be : response.getEntries()) {
                ListEntry le = (ListEntry) be;
                S3Object obj = S3Object.loadedFromServer(this, le.key);
                obj.setLastModified(le.lastModified);
                obj.setSize(le.size);
                retVal.add(obj);
            }
        }

        return retVal;
    }

    public S3Object find(String key) throws IOException {
        for (S3Object obj : getObjects())
            if (obj.getKey().equals(key)) {
                obj.refreshMeta();
                return obj;
            }
        return null;
    }


    /**
     * update data for the object
     *
     * @param object
     * @throws IOException
     */
    public void refreshObject(S3Object object) throws IOException {
        com.amazon.s3.S3Object obj = awsAuthConnection.get(name, object.getKey(), null).object;
        object.setTransient(obj == null);
        if (!object.isTransient()) {
            object.setData(obj.data);
            object.setMeta(obj.metadata);
        }
    }

    public String getSignedUrl(S3Object object) {
        queryStringGenerator.setExpiresIn(60000);
        return queryStringGenerator.get(name, object.getKey(), null);
    }

    public void refreshObjectMeta(S3Object object) throws IOException {
        if (object.isTransient())
            throw new RuntimeException("transient object cannot updated, check transient status first");
        com.amazon.s3.S3Object obj = awsAuthConnection.head(name, object.getKey(), null).object;
        if (obj == null)
            object.setTransient(true);
        else
            object.setMeta(obj.metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        S3BucketImpl s3Bucket = (S3BucketImpl) o;

        if (name != null ? !name.equals(s3Bucket.name) : s3Bucket.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = awsAuthConnection != null ? awsAuthConnection.hashCode() : 0;
        result = 31 * result + (queryStringGenerator != null ? queryStringGenerator.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
