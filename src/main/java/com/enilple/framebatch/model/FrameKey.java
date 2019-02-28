package com.enilple.framebatch.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Embeddable
public class FrameKey implements Serializable {

    private static final long serialVersionUID = -7208131625722980090L;

    @Column(name = "MEDIA_SCRIPT_NO")
    private int mediaScriptNo;

    @Column(name = "ALGM_SEQ")
    private int algmSeq;

    @Column(name = "PRDT_TP_CODE")
    private String prdtTpCode;

    @Column(name = "FRME_CODE")
    private String frameCode;

    @Column(name = "CYCLE_TRN")
    private int cycleTrn;

    @Builder
    private FrameKey(int mediaScriptNo, int algmSeq, String prdtTpCode, String frameCode, int cycleTrn) {
        this.mediaScriptNo = mediaScriptNo;
        this.algmSeq = algmSeq;
        this.prdtTpCode = prdtTpCode;
        this.frameCode = frameCode;
        this.cycleTrn = cycleTrn;
    }
}
