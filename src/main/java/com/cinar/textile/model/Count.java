package com.cinar.textile.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "tbl_count")
@Embeddable
public class Count {
    private Integer weftStopCount;
    private Integer warpStopCount;
    private Integer lenoStopCount;
    private Integer otherStopCount;
    private Integer operationCount;

}
