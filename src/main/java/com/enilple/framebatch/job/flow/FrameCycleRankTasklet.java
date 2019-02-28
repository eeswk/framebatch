package com.enilple.framebatch.job.flow;

import com.enilple.framebatch.model.FrameCycleStats;
import com.enilple.framebatch.model.FrameCycleStatsRank;
import com.enilple.framebatch.repository.FrameCycleStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@StepScope
public class FrameCycleRankTasklet implements Tasklet {

    @Autowired
    private FrameCycleStatsRepository repository;


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("FrameCycleRankTasklet");

        List<FrameCycleStats> frameCycleStats = repository.findByFrameKey(17258, 2, "01");

        Map<Integer, List<FrameCycleStats>> framCycleTrns = frameCycleStats.stream()
                .collect(groupingBy(f -> f.getId().getCycleTrn()));
        List<FrameCycleStatsRank> frameCycleStatsList =  framCycleTrns.entrySet().stream()
                .map(entry -> {
                    List<FrameCycleStats>  entrylist = entry.getValue();

                    Optional<FrameCycleStats> f =  entrylist.stream().max(Comparator.comparingDouble(l -> Math.round((double)(l.getClickCnt()) / l.getParEprsCnt() * 100 *10000 )/10000.0));
                    FrameCycleStatsRank frameCycleStatsRank = new FrameCycleStatsRank(f.get().getId(), 1, Math.round((double) (f.get().getClickCnt()) / f.get().getParEprsCnt() * 100 * 10000) / 10000.0);
                    return frameCycleStatsRank;
                }).collect(toList());

        log.info("frameCycleStatsList = {}", frameCycleStatsList);
        log.info("frameCycleStatsListSize = {}", frameCycleStatsList.size());

        //frameCycleStatsList.forEach(f -> frameCycleStatsRankRepository.save(f));
        //frameCycleStatsRankRepository.saveAll(frameCycleStatsList);



        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("frameCycleRankList", frameCycleStatsList);

        return RepeatStatus.FINISHED;
    }
}
