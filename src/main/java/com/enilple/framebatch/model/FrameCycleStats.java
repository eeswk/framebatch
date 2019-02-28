package com.enilple.framebatch.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "frme_cycle_log")
@ToString
public class FrameCycleStats {

    @Id
    @EmbeddedId
    private FrameKey id;

    @Column(name = "PAR_EPRS_CNT")
    private int parEprsCnt;

    @Column(name = "CLICK_CNT")
    private int clickCnt;

    @Column(name = "FRME_CLICK_CNT")
    private int frameClickCnt;

    @Column(name = "ADVRTS_AMT")
    private BigDecimal advrtsAmt;

    @Column(name = "ORDER_AMT")
    private int orderAmt;

    @Column(name = "ORDER_CNT")
    private int orderCnt;

    @Column(name = "REG_USER_ID")
    private String regUserId;

    @CreationTimestamp
    @Column(name = "REG_DTTM")
    private LocalDateTime regDttm;

    @Column(name = "ALT_USER_ID")
    private String altUserId;

    @UpdateTimestamp
    @Column(name = "ALT_DTTM")
    private LocalDateTime altDttm;


}


