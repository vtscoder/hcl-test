package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface Server {
    boolean saveDataEnvelope(String requestCheckSum, DataEnvelope envelope) throws Exception;

    List<com.db.dataplatform.techtest.server.dto.DataBody> getDataByBlockType(BlockTypeEnum blockType);
}
