package com.db.dataplatform.techtest.server.dto;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataHeader {
    private Long id;
    private String name;
    private String blocktype;
    private Instant createdTimestamp;
}
