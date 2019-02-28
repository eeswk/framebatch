package com.enilple.framebatch.repository;

import com.enilple.framebatch.model.FrameCyclePdct;
import com.enilple.framebatch.model.FrameCycleStatsRank;
import com.enilple.framebatch.model.FrameKey;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FrameCyclePdctRepository extends PagingAndSortingRepository<FrameCyclePdct, FrameKey> {

}
