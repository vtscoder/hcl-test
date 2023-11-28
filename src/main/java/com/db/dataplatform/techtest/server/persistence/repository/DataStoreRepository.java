package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {

    @Query("select dbe from DataBodyEntity dbe, DataHeaderEntity dhe where dhe.dataBodyEntity.id = dbe.id and dhe.blocktype=:blockType")
    List<DataBodyEntity> findAllByDataHeaderBlockType(@Param("blockType") BlockTypeEnum blockType);
}
