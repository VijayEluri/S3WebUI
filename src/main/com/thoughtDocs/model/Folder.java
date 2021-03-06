package com.thoughtDocs.model;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ThoughtWorks
 * Date: Jul 29, 2009
 * Time: 11:31:25 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Folder extends Item {

    List<Item> getItems() throws IOException;

    Repository getRepository();

    /**
     * Whether items can be created underneath it
     * @return
     */
    boolean getAllowNewItem();
   
    
}
                      