package com.db.dataplatform.techtest.server.dto;

import lombok.*;
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
