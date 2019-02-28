package com.enilple.framebatch.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class FrameCycleStatsRank {

    @Id
    @EmbeddedId
    private FrameKey id;

    @Column(name = "RANK")
    private int rank;

    @Column(name = "CTR")
    private double ctr;

    @Builder
    public FrameCycleStatsRank(FrameKey id, int rank, double ctr) {
        this.id = id;
        this.rank = rank;
        this.ctr = ctr;
    }
}


