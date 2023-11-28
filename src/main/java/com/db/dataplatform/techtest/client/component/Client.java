package com.db.dataplatform.techtest.client.component;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.dto.DataBody;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface Client {
    void pushData(DataEnvelope dataEnvelope) throws Exception;
    List<DataBody> getData(String blockType);
    void patchData(String blockName, String newBlockType) throws UnsupportedEncodingException;
}
