package com.enilple.framebatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor    //생성자 DI를 위한 lombok
@Configuration
public class SimpleJobConfiguration {
    
    private final JobBuilderFactory jobBuilderFactory; // 생성자 DI 받음
    private final StepBuilderFactory stepBuilderFactory; //생성자 DI 받음

    private final SimpleJobTasklet tasklet3;
    
    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .next(simpleStep3())
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    ///throw new IllegalArgumentException("Step1에서 실패");
                    log.info(">>>> This is Step1");
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet(scopeStep2Tasklet(null))
                /*.tasklet((stepContribution, chunkContext) -> {
                    log.info(">>>> This is Stemp2");
                    log.info(">>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })*/
                .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate) {

        return (stepContribution, chunkContext) -> {
            log.info(">>>> This is scopeStep2");
            log.info(">>>> requestDate = {}", requestDate);
            return RepeatStatus.FINISHED;
        };
    }



    @Bean
    @JobScope
    public Step simpleStep3() {
        return stepBuilderFactory.get("simpleStep3")
                .tasklet(tasklet3)
                .build();
    }
}
