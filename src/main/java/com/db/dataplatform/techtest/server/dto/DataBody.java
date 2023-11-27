package com.db.dataplatform.techtest.server.dto;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataBody {
    private Long id;
    private DataHeader dataHeader;
    private String dataBody;
    private String dataCheckSum;
    private Instant createdTimestamp;
}
