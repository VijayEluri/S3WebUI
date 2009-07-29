package com.thoughtDocs.model.impl.s3;

import com.thoughtDocs.model.Document;
import com.thoughtDocs.model.Repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kailuo "Kai" Wang
 * Date: Jul 10, 2009
 * Time: 6:29:24 PM
 */
public class DocumentImpl implements Document, Serializable {

    private static final String PUBLIC_PASSWORD_META_KEY = "public-password";
    private S3Object s3Object;

    public DocumentImpl(S3Object obj) {
       this.s3Object = obj;
    }

    public boolean isTransient(){
        return s3Object.isTransient();
    }

    public static Document createTransientDocument(Repository repo, String name){
        S3Object obj = S3Object.createNewTransient(((RepositoryImpl)repo).getBucket(), name);
        return new DocumentImpl(obj);
    }

    /**
     * get the document by name from server
     * @param repo
     * @param name
     * @return null if no such file found on server
     * @throws IOException
     */
    public static Document loadedFromRepository(Repository repo, String name) throws IOException {
        S3Object obj = S3Object.loadedFromServer(((RepositoryImpl)repo).getBucket(), name);
        obj.updateMeta();
        if(obj.isTransient())
            return null;
        return new DocumentImpl(obj);
    }

    public String getName() {
        return s3Object.getKey();
    }

    public void setName(String newName) {
        s3Object.setKey(newName);
    }


    public String getSignedURL() {
        return s3Object.getSignedURL();
    }

    public byte[] getData() throws IOException {
        if(s3Object.getData() == null && !isTransient())
          update();
        return s3Object.getData();
    }

    public void setData(byte[] data) {
        s3Object.setData(data);
    }

    public String getContentType() {
        return  s3Object.getContentType();
    }

    public void setContentType(String contentType) {
        s3Object.setContentType(contentType);
    }

    public void delete() throws IOException {
        s3Object.delete();
    }

    public String getPassword() throws IOException {
        Object passwordsMeta = s3Object.getMeta().get(PUBLIC_PASSWORD_META_KEY);
            if (passwordsMeta != null)
                return  (String) ((List) passwordsMeta).get(0);
            else
                return null;
    }


    public void setPassword(String password) throws IOException {
        s3Object.getMeta().put(PUBLIC_PASSWORD_META_KEY, Arrays.asList(password));
    }

    public void save() throws IOException {
        s3Object.save();
    }

    public void update() throws IOException {
        s3Object.update();
    }

    
    /**
     *
     * @return public url for external user to download (password needed)
     */
    public String getPublicUrl(){
         return "http://thoughtfiles.com/" + getName() ;
    }

}
