package com.thoughtDocs.viewModel;

import com.thoughtDocs.model.Document;
import com.thoughtDocs.model.Repository;
import com.thoughtDocs.model.SecurityMode;
import com.thoughtDocs.model.impl.s3.DocumentImpl;
import com.thoughtDocs.viewModel.itemList.DocumentListAction;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import java.io.IOException;
import java.io.Serializable;

@Name("documentUploadAction")
public class DocumentUploadAction implements Serializable {

    @Logger
    private Log log;

    @In
    private StatusMessages statusMessages;

    @In
    DocumentListAction documentListAction;


    @In
    private Repository defaultRepository;
    private String password;
    private String name;
    private String contentType;
    private byte[] data;
    private SecurityMode securityMode;


    public void upload() throws IOException {
        Document doc = DocumentImpl.createTransientDocument(documentListAction.getCurrentFolder(), name);
        doc.setPassword(password);
        doc.setContentType(contentType);
        doc.setData(data);
        doc.setSecurityMode(securityMode);
        doc.save();
        documentListAction.getDisplayItems();
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setSecurityMode(SecurityMode securityMode) {
        this.securityMode = securityMode;
    }

    public SecurityMode getSecurityMode() {
        return securityMode;
    }
}

