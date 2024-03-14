package com.cinar.textile.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "machines")

public class Machine extends BaseEntity {

    private String shiftName;
    private LocalDateTime shiftDate;
    private LocalDateTime month;
    private LocalDateTime year;
    private Integer shiftNo;
    private Long pickCounter;
    @Embedded

    @AttributeOverrides({@AttributeOverride(name = "weftStopCount", column = @Column(name = "weft_stop_count", nullable = false))
            ,@AttributeOverride(name = "warpStopCount", column = @Column(name = "warp_stop_count", nullable = false)),
            @AttributeOverride(name = "lenoStopCount", column = @Column(name = "leno_stop_count", nullable = false)),
            @AttributeOverride(name = "otherStopCount", column = @Column(name = "other_stop_count", nullable = false)),
            @AttributeOverride(name = "operationCount", column = @Column(name = "operation_count", nullable = false))})
    private Count count;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "runningDuration", column = @Column(name = "running_duration", nullable = false)),
            @AttributeOverride(name = "welfStopDuration", column = @Column(name = "welf_stop_duration", nullable = false)),
            @AttributeOverride(name = "warnStopDuration", column = @Column(name = "warn_stop_duration", nullable = false))})
    private Duration duration;


    public Machine(String shiftName, LocalDateTime shiftDate, LocalDateTime month,
                   LocalDateTime year, Integer shiftNo, Long pickCounter,Integer weftStopCount,
                   Integer warpStopCount, Integer lenoStopCount, Integer otherStopCount,
                   Integer operationCount,Double runningDuration, Double welfStopDuration, Double warnStopDuration

    ) {
        this.shiftName = shiftName;
        this.shiftDate = shiftDate;
        this.month = month;
        this.year = year;
        this.shiftNo = shiftNo;
        this.pickCounter = pickCounter;
        this.count = new Count(weftStopCount,warpStopCount,lenoStopCount,otherStopCount,operationCount);
        this.duration = new Duration(runningDuration,welfStopDuration,warnStopDuration);
    }
}
