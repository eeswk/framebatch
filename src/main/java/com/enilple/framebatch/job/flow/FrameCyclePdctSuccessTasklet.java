package com.enilple.framebatch.job.flow;

import com.enilple.framebatch.model.FrameCyclePdct;
import com.enilple.framebatch.model.FrameCycleStats;
import com.enilple.framebatch.model.FrameCycleStatsRank;
import com.enilple.framebatch.repository.FrameCyclePdctRepository;
import com.enilple.framebatch.repository.FrameCycleStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@StepScope
public class FrameCyclePdctSuccessTasklet implements Tasklet {

    @Autowired
    private FrameCyclePdctRepository repository;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        log.info("FrameCyclePdctSuccess");

        List<FrameCycleStatsRank> frameCycleRankList = (List<FrameCycleStatsRank>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("frameCycleRankList");

        frameCycleRankList.stream().sorted((v1, v2) -> {
            return v1.getId().getCycleTrn() - v2.getId().getCycleTrn();
        });
        List<FrameCyclePdct> frameCyclePdctList =  new ArrayList<>();

        String frameCode = "";
        for (FrameCycleStatsRank frameCycleStatsRank : frameCycleRankList) {
            boolean pdctSucc = false;
            if (frameCycleStatsRank.getId().getFrameCode().equals(frameCode)) pdctSucc = true;

            frameCode = frameCycleStatsRank.getId().getFrameCode();
            frameCyclePdctList.add(new FrameCyclePdct(frameCycleStatsRank.getId(), pdctSucc));
        }

        log.info("frameCyclePdctList = {}", frameCyclePdctList);
        log.info("frameCyclePdctListSize = {}", frameCyclePdctList.size());

        //frameCyclePdctList.forEach(f -> repository.save(f));
        repository.saveAll(frameCyclePdctList);

        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("frameCyclePdctList", frameCyclePdctList);

        return RepeatStatus.FINISHED;
    }
}
