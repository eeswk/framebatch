package com.enilple.framebatch.job;

import com.enilple.framebatch.job.flow.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor    //생성자 DI를 위한 lombok
public class FrameBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final FrameCycleTasklet frameCycleTasklet;
    private final FrameCycleRankTasklet frameCycleRankTasklet;
    private final FrameCycleRankTasklet2 frameCycleRankTasklet2;
    private final FrameCyclePdctSuccessTasklet2 frameCyclePdctSuccessTasklet2;
    private final FrameCyclePdctSuccessTasklet frameCyclePdctSuccessTasklet;



    @Bean
    public Job frameCycleRankJob() {
        return jobBuilderFactory.get("frameCycleJob")
//                .start(frameCycleStep())
                .start(partitionStep())
                //.next(frameCyclePdctSuccessStep2())
               // .next(frameCycleRankStep())
                //.next(frameCyclePdctSuccessStep())
                .build();
    }

    @Bean
    @JobScope
    public Step frameCycleStep() {
        return stepBuilderFactory.get("frameCycleStep")
                .tasklet(frameCycleTasklet)
                .build();
    }

    @Bean
    public Step partitionStep() {
        return stepBuilderFactory.get("partitionStep")
                .partitioner("partition-work", partitioner())
                .gridSize(10)
                .step(frameCycleRankStep2())
                .allowStartIfComplete(true)
                .build();

    }

    @Bean
    public Partitioner partitioner () {
        return new BasicPartitioner();
    }

    @Bean
    @JobScope
    public Step frameCycleRankStep2() {
        return stepBuilderFactory.get("frameCycleRankStep2")
                .tasklet(frameCycleRankTasklet2)
                .build();
    }


    @Bean
    @JobScope
    public Step frameCycleRankStep() {
        return stepBuilderFactory.get("frameCycleRankStep")
                .tasklet(frameCycleRankTasklet)
//                .taskExecutor(executor())
                .build();
    }

//    public static final int CORE_TASK_POOL_SIZE = 24;
//    public static final int MAX_TASK_POOL_SIZE = 128;
//
//    @Bean(name = "candidateTaskPool")
//    public TaskExecutor executor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(CORE_TASK_POOL_SIZE);
//        executor.setMaxPoolSize(MAX_TASK_POOL_SIZE);
//        return executor;
//    }

    @Bean
    @JobScope
    public Step frameCyclePdctSuccessStep() {
        return stepBuilderFactory.get("frameCyclePdctSuccessStep")
                .tasklet(frameCyclePdctSuccessTasklet)
                .build();
    }

    @Bean
    @JobScope
    public Step frameCyclePdctSuccessStep2() {
        return stepBuilderFactory.get("frameCyclePdctSuccessStep2")
                .tasklet(frameCyclePdctSuccessTasklet2)
                .build();
    }

}
