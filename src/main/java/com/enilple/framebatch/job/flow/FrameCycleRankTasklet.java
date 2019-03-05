package com.enilple.framebatch.job.flow;

import com.enilple.framebatch.model.FrameCycleStats;
import com.enilple.framebatch.model.FrameCycleStatsRank;
import com.enilple.framebatch.repository.FrameCycleStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@StepScope
public class FrameCycleRankTasklet implements Tasklet {

    @Autowired
    private FrameCycleStatsRepository repository;

/*
    private List<FrameCycleStats> frameCycleStatsArray;


    private int minValue;

    @BeforeStep
    void beforeStep(final StepExecution stepExecution) {
        //executionContext에 저장 된 페이지 정보 꺼냄
        frameCycleStatsArray = (List<FrameCycleStats>) stepExecution.getExecutionContext().get("frameCycleStatsList");
        minValue = (int) stepExecution.getExecutionContext().get("frameStatsPartition");

    }
*/

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("FrameCycleRankTasklet");
        List<Object[]> frameCycleStatsArray = (List<Object[]>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("frameCycleGroupList");


        log.info("frameCycleStatsArray = {}", frameCycleStatsArray);
        List<FrameCycleStatsRank> frameCycleStatsAll = new ArrayList<>();

        frameCycleStatsArray.forEach(arr -> {


            int ms = (int) arr[0];
            int algmSeq = (int) arr[1];
            String prdtTpcode = (String) arr[2];

            log.info("ms = {}, algmSeq = {}, prdtTpcode = {}", ms, algmSeq, prdtTpcode);

            List<FrameCycleStats> frameCycleStats = repository.findByFrameKey(ms, algmSeq, prdtTpcode);

            Map<Integer, List<FrameCycleStats>> framCycleTrns = frameCycleStats.stream()
                    .collect(groupingBy(f -> f.getId().getCycleTrn()));
            List<FrameCycleStatsRank> frameCycleStatsList =  framCycleTrns.entrySet().stream()
                    .map(entry -> {
                        List<FrameCycleStats>  entrylist = entry.getValue();

                        Optional<FrameCycleStats> fr =  entrylist.stream().max(Comparator.comparingDouble(l -> Math.round((double)(l.getClickCnt()) / l.getParEprsCnt() * 100 *10000 )/10000.0));
                        FrameCycleStatsRank frameCycleStatsRank = new FrameCycleStatsRank(fr.get().getId(), 1, Math.round((double) (fr.get().getClickCnt()) / fr.get().getParEprsCnt() * 100 * 10000) / 10000.0);
                        return frameCycleStatsRank;
                    }).collect(toList());

            //log.info("frameCycleStatsList = {}", frameCycleStatsList);
            log.info("frameCycleStatsListSize = {}", frameCycleStatsList.size());
            frameCycleStatsAll.addAll(frameCycleStatsList);

        });

        log.info("frameCycleStatsAllsize = {}", frameCycleStatsAll.size());


        //frameCycleStatsList.forEach(f -> frameCycleStatsRankRepository.save(f));
        //frameCycleStatsRankRepository.saveAll(frameCycleStatsList);



        //chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("frameCycleRankList", frameCycleStatsList);

        return RepeatStatus.FINISHED;
    }
}
