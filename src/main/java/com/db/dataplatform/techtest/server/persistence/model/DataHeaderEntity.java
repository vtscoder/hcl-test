package com.db.dataplatform.techtest.server.persistence.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "DATA_HEADER",
        uniqueConstraints = @UniqueConstraint(columnNames="NAME")
)
@Setter
@Getter
public class DataHeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_HEADER_ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "BLOCKTYPE")
    @Enumerated(EnumType.STRING)
    private BlockTypeEnum blocktype;

    @Column(name = "CREATED_TIMESTAMP")
    private Instant createdTimestamp;

    @PrePersist
    public void setTimestamps() {
        if (createdTimestamp == null) {
            createdTimestamp = Instant.now();
        }
    }
}