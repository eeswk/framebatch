package com.enilple.framebatch.model;

import com.enilple.framebatch.model.util.BooleanToYNConverter;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.*;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "frme_cycle_pdct")
@ToString
@RequiredArgsConstructor
public class FrameCyclePdct {

    @Id
    @EmbeddedId
    private FrameKey id;

    @Column(name = "PDCT_SUCES_YN")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean prdcSuccess;

    @Column(name = "REG_USER_ID", insertable = false, updatable = false)
    private String regUserId;

    @CreationTimestamp
    @Column(name = "REG_DTTM")
    private LocalDateTime regDttm;

    @Column(name = "ALT_USER_ID")
    private String altUserId;

    @UpdateTimestamp
    @Column(name = "ALT_DTTM")
    private LocalDateTime altDttm;


    @Builder
    public FrameCyclePdct(FrameKey id, boolean prdcSuccess) {
        this.id = id;
        this.prdcSuccess = prdcSuccess;
    }
}


