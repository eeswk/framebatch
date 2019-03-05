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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@StepScope
public class FrameCycleRankTasklet2 implements Tasklet {

    @Autowired
    private FrameCycleStatsRepository repository;

    @Autowired
    private FrameCyclePdctRepository frameCyclePdctRepository;

    @Value("#{stepExecutionContext[minValue]}") Integer minValue;
    @Value("#{stepExecutionContext[name]}") String name;
    @Value("#{stepExecutionContext[frameCycleStatsList]}") List<Object[]> list;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        log.info("minValue = {}" , minValue);
        log.info("list = {}" , list);
        log.info("list = {}" , (Object) list.get(0)[0]);

        List<FrameCycleStatsRank> frameCycleStatsAll = new ArrayList<>();

        list.forEach(arr -> {
            int ms = (int) arr[0];
            int algmSeq = (int) arr[1];
            String prdtTpcode = (String) arr[2];

            log.info("ms = {}, algmSeq = {}, prdtTpcode = {}", ms, algmSeq, prdtTpcode);
            //사이크별 리스트 조회
            List<FrameCycleStats> frameCycleStats = repository.findByFrameKey(ms, algmSeq, prdtTpcode);

            //1등랭키변경
            List<FrameCycleStatsRank> frameCycleStatsRanks = getFrameRankListByFrameCycleTrns(frameCycleStats);

            log.info("{}-{}, ms = {}, algmSeq = {}, prdtTpcode = {}, size = {}", "FrameCycleStatsRanks ", name, ms, algmSeq, prdtTpcode,frameCycleStatsRanks.size());

            //전 사이클과 비교후 예측확인
            List<FrameCyclePdct> frameCyclePdctList = frameCycleRankCompareByBeforeCycleNum(frameCycleStatsRanks);

            //저장
            frameCyclePdctRepository.saveAll(frameCyclePdctList);

            //저장개수
            frameCycleStatsAll.addAll(frameCycleStatsRanks);

        });

        log.info("frameCycleStatsAllsize = {} - {}", name, frameCycleStatsAll.size());

        return RepeatStatus.FINISHED;
    }

    //1등 프레임을 추출
    private List<FrameCycleStatsRank> getFrameRankListByFrameCycleTrns(List<FrameCycleStats> frameCycleStats) {
        Map<Integer, List<FrameCycleStats>> framCycleTrns = frameCycleStats.stream()
                .collect(groupingBy(f -> f.getId().getCycleTrn()));

        List<FrameCycleStatsRank> frameCycleStatsList =  framCycleTrns.entrySet().stream()
                .map(entry -> {
                    List<FrameCycleStats>  entrylist = entry.getValue();

                    Optional<FrameCycleStats> fr =  entrylist.stream().max(Comparator.comparingDouble(l -> Math.round((double)(l.getClickCnt()) / l.getParEprsCnt() * 100 *10000 )/10000.0));
                    FrameCycleStatsRank frameCycleStatsRank = new FrameCycleStatsRank(fr.get().getId(), 1, Math.round((double) (fr.get().getClickCnt()) / fr.get().getParEprsCnt() * 100 * 10000) / 10000.0);
                    return frameCycleStatsRank;
                }).collect(toList());
        return frameCycleStatsList;
    }


    private List<FrameCyclePdct> frameCycleRankCompareByBeforeCycleNum(List<FrameCycleStatsRank> frameCycleRankList) {

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
        return frameCyclePdctList;

    }
}
