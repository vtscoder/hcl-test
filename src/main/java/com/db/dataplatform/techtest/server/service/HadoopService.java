package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.exception.DownStreamPushDataException;

public interface HadoopService {
    void pushDataToHadoop(String payload) throws DownStreamPushDataException;
}
