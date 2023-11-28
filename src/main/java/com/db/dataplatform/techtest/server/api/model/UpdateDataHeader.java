package com.db.dataplatform.techtest.server.api.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonSerialize(as = UpdateDataHeader.class)
@JsonDeserialize(as = UpdateDataHeader.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class UpdateDataHeader {

    @NotNull
    private BlockTypeEnum blockType;

}
