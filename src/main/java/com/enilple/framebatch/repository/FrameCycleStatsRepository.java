package com.enilple.framebatch.repository;

import com.enilple.framebatch.model.FrameCycleStats;
import com.enilple.framebatch.model.FrameKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameCycleStatsRepository extends PagingAndSortingRepository<FrameCycleStats, FrameKey> {

    @Query("SELECT p FROM FrameCycleStats p WHERE p.id.mediaScriptNo = :ms and p.id.algmSeq = :algmSeq and  p.id.prdtTpCode = :prdtTpCode")
    List<FrameCycleStats> findByFrameKey(int ms, int algmSeq, String prdtTpCode);

    @Query("SELECT  p.id.mediaScriptNo, p.id.algmSeq, p.id.prdtTpCode FROM FrameCycleStats p " +
            " WHERE p.id.algmSeq IN (2, 3, 4) group by p.id.mediaScriptNo, p.id.algmSeq, p.id.prdtTpCode ")
    List<FrameCycleStats> findAllbyGroupBy();

}
