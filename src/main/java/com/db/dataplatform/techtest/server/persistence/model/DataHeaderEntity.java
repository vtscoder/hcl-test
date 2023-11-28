package com.db.dataplatform.techtest.server.persistence.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "DATA_HEADER",
        uniqueConstraints = @UniqueConstraint(columnNames="NAME")
)
@Setter
@Getter
@ToString
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DATA_STORE_ID")
    private DataBodyEntity dataBodyEntity;

    @PrePersist
    public void setTimestamps() {
        if (createdTimestamp == null) {
            createdTimestamp = Instant.now();
        }
    }
}
