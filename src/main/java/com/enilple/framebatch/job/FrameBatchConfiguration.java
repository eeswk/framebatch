package com.enilple.framebatch.job;

import com.enilple.framebatch.job.flow.FrameCyclePdctSuccessTasklet;
import com.enilple.framebatch.job.flow.FrameCycleRankTasklet;
import com.enilple.framebatch.job.flow.FrameCycleTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor    //생성자 DI를 위한 lombok
public class FrameBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final FrameCycleTasklet frameCycleTasklet;
    private final FrameCycleRankTasklet frameCycleRankTasklet;
    private final FrameCyclePdctSuccessTasklet frameCyclePdctSuccessTasklet;



    @Bean
    public Job frameCycleRankJob() {
        return jobBuilderFactory.get("frameCycleRankJob")
                .start(frameCycleRankStep())
                .next(frameCyclePdctSuccessStep())
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
    @JobScope
    public Step frameCycleRankStep() {
        return stepBuilderFactory.get("frameCycleRankStep")
                .tasklet(frameCycleRankTasklet)
                .build();
    }

    @Bean
    @JobScope
    public Step frameCyclePdctSuccessStep() {
        return stepBuilderFactory.get("frameCyclePdctSuccessStep")
                .tasklet(frameCyclePdctSuccessTasklet)
                .build();
    }

}
