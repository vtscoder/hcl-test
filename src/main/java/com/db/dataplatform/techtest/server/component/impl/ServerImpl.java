package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.CheckSumNotMatchingException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.util.MD5Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataStoreRepository dataStoreRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(String requestCheckSum, DataEnvelope envelope) throws Exception{

        String dataCheckSum = MD5Utils.checkSum(objectMapper, envelope);

        if( !dataCheckSum.equals(requestCheckSum)){
            throw new CheckSumNotMatchingException("Server check sum not matching with client checksum");
        }

        // Save to persistence.
        DataBodyEntity dataBodyEntity =  buildEntity(requestCheckSum, envelope);

        dataStoreRepository.save(dataBodyEntity);

        log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
        return true;
    }

    @Override
    public List<com.db.dataplatform.techtest.server.dto.DataBody> getDataByBlockType(BlockTypeEnum blockType)
    {
        List<DataBodyEntity> dataBodyEntities = dataStoreRepository.findAllByDataHeaderBlockType(blockType);
        log.info("data retrieved for blockType: {}, size {}", blockType, dataBodyEntities.size());
        return dataBodyEntities.stream()
                .map(this::buildDataBody)
                .collect(Collectors.toList());
    }

    private com.db.dataplatform.techtest.server.dto.DataBody buildDataBody(DataBodyEntity dataBodyEntity){
        com.db.dataplatform.techtest.server.dto.DataHeader dataHeader =  modelMapper.map(dataBodyEntity.getDataHeaderEntity(), com.db.dataplatform.techtest.server.dto.DataHeader.class);
        com.db.dataplatform.techtest.server.dto.DataBody dataBody = modelMapper.map(dataBodyEntity, com.db.dataplatform.techtest.server.dto.DataBody.class);
        dataBody.setDataHeader(dataHeader);
        return dataBody;
    }

    private DataBodyEntity buildEntity(String requestCheckSum, DataEnvelope envelope) {
        log.info("building data entity: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataCheckSum(requestCheckSum);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        return  dataBodyEntity;
    }
}
