package com.enilple.framebatch.job.flow;

import com.enilple.framebatch.model.FrameCyclePdct;
import com.enilple.framebatch.model.FrameCycleStatsRank;
import com.enilple.framebatch.repository.FrameCyclePdctRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@StepScope
public class FrameCyclePdctSuccessTasklet2 implements Tasklet {

    @Autowired
    private FrameCyclePdctRepository repository;


    @Value("#{stepExecutionContext[frameCycleStatsAll]}") List<FrameCycleStatsRank[]> ranks;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        log.info("ranks = {}" ,ranks);

        log.info("FrameCyclePdctSuccess2");

        List<FrameCycleStatsRank> frameCycleStatsAllsize = (List<FrameCycleStatsRank>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("frameCycleStatsAll");

        log.info("FrameCyclePdctSuccess2 size = {} , ", frameCycleStatsAllsize.size());


        return RepeatStatus.FINISHED;
    }
}
