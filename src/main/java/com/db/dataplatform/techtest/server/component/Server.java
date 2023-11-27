package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface Server {
    boolean saveDataEnvelope(String requestCheckSum, DataEnvelope envelope) throws Exception;
}
