package com.enilple.framebatch.reader.jdbc;

import com.enilple.framebatch.model.FrameCycleStats;
import com.enilple.framebatch.model.FrameCycleStatsRank;
import com.enilple.framebatch.model.FrameKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderFrameCycleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Job jpaPagingItemReaderFrameCycleJob() {
        return jobBuilderFactory.get("jpaPagingItemReaderFrameCycleJob")
                .start(jpaPagingItemReaderFrameCycleStep())
                .build();

    }

    @Bean
    public Step jpaPagingItemReaderFrameCycleStep() {
        return stepBuilderFactory.get("jpaPagingItemReaderFrameCycleStep")
                .<FrameCycleStats, FrameCycleStats>chunk(CHUNK_SIZE)
                .reader(jpaPagingItemReaderFrameCycleReader())
                .writer(jpaPagingItemReaderFrameCycleWriter())
                .build();
    }



    private ItemWriter<FrameCycleStats> jpaPagingItemReaderFrameCycleWriter() {

        return list -> {
/*            String frameCode = "";
            List<FrameCycleStatsRank> frameCycleStatsRankList = new ArrayList<>();
            for (FrameCycleStats frameCycleStats : list) {
                log.info("frameCycleStats = {}", frameCycleStats);
                if (frameCycleStats.getId().getFrameCode().equals(frameCode)) {
                    FrameCycleStatsRank frameCycleStatsRank = new FrameCycleStatsRank();
                    frameCycleStatsRank.setId(frameCycleStats.getId());
                    frameCycleStatsRank.setPredictYn(true);
                    frameCycleStatsRankList.add(frameCycleStatsRank);
                }
            }

            for (FrameCycleStatsRank frameCycleStatsRank : frameCycleStatsRankList) {
                log.info("frameCycleStatsRank = {}", frameCycleStatsRank);
            }*/

            Map<Integer, List<FrameCycleStats>> framCycleTrns = list.stream()
                    .collect(groupingBy(f -> f.getId().getCycleTrn()));
            System.out.println(framCycleTrns.size());
            System.out.println(framCycleTrns);
            List<FrameCycleStatsRank> frameCycleStatsList =  framCycleTrns.entrySet().stream()
                    .map(entry -> {
                        List<FrameCycleStats>  entrylist = entry.getValue();
   /*                     entrylist.forEach( x -> {
                            System.out.println(x.getId().getFrameCode() + ", " + x.getId().getCycleTrn() + ", " + x.getClickCnt()
                                    + " ," + x.getParEprsCnt() + " ,"+ Math.round((double)(x.getClickCnt()) / x.getParEprsCnt() * 100 *10000 )/10000.0);
                        });*/
                        Optional<FrameCycleStats> f =  entrylist.stream().max(Comparator.comparingDouble(l -> Math.round((double)(l.getClickCnt()) / l.getParEprsCnt() * 100 *10000 )/10000.0));
                        System.out.println(f.get().getId().getFrameCode());

/*                        rank.setId(FrameKey.builder()
                                .mediaScriptNo(f.get().getId().getMediaScriptNo())
                                .algmSeq(f.get().getId().getAlgmSeq())
                                .cycleTrn(f.get().getId().getCycleTrn())
                                .frameCode(f.get().getId().getFrameCode())
                                .build());
                        rank.setRank(1);*/

                        FrameCycleStatsRank frameCycleStatsRank = new FrameCycleStatsRank(f.get().getId(), 1, Math.round((double) (f.get().getClickCnt()) / f.get().getParEprsCnt() * 100 * 10000) / 10000.0);
                        return frameCycleStatsRank;
                    }).collect(toList());

/*            frameCycleStatsList.forEach(
                    x -> {
                        System.out.println(x.getId().getFrameCode() + ", " + x.getId().getCycleTrn() + ", " + x.getClickCnt()
                                + " ," + x.getParEprsCnt() + " ,"+ x.getRank());
                    });*/
        };
    }

    @Bean
    public JpaPagingItemReader<FrameCycleStats> jpaPagingItemReaderFrameCycleReader() {
        return new JpaPagingItemReaderBuilder<FrameCycleStats>()
                .name("jpaPagingItemReaderFrameCycleReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT p FROM FrameCycleStats p WHERE p.id.mediaScriptNo = 17258 and p.id.algmSeq = 2 and  p.id.prdtTpCode = 01")
                .build();
    }
}
