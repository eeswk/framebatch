package com.enilple.framebatch.job.flow;

import com.enilple.framebatch.model.FrameCycleStats;
import com.enilple.framebatch.repository.FrameCycleStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Component
@StepScope
public class FrameCycleTasklet implements Tasklet {

    @Autowired
    private FrameCycleStatsRepository repository;


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("FrameCycleTasklet");

        List<FrameCycleStats> frameCycleGroupList = repository.findAllbyGroupBy();

        log.info("frameCycleGroupList size = {}", frameCycleGroupList.size());

        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("frameCycleGroupList", frameCycleGroupList);

        return RepeatStatus.FINISHED;
    }
}
