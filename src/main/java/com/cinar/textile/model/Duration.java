package com.cinar.textile.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "tbl_address")
@Embeddable
public class Duration {
    private Double runningDuration;
    private Double welfStopDuration;
    private Double warnStopDuration;


}
