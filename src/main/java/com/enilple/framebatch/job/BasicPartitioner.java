package com.enilple.framebatch.job;

import com.enilple.framebatch.model.FrameCycleStats;
import com.enilple.framebatch.repository.FrameCycleStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BasicPartitioner implements  Partitioner {

    private static final String PARTITION_KEY = "partition";

    @Autowired
    private FrameCycleStatsRepository repository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        log.info("repository = {}", repository);
        List<FrameCycleStats> frameCycleGroupList = repository.findAllbyGroupBy();

        int min = 1;
        int max = frameCycleGroupList.size(); //100 //gridSize 10

        log.info("frameCycleGroupList size = {}", max);

        int targetSize = (max - min) / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<>();
        int number = 0;
        int start = min;
        int end = start + targetSize -1;

        while (start <= max) {
            if (end >= max) {
                end = max;
            }
            ExecutionContext value = new ExecutionContext();
            List<FrameCycleStats> list = new ArrayList<FrameCycleStats>();
            for(int i = start; i <= end ;) {
                //value.put("frameStats"+i, frameCycleGroupList.get(i));

                list.add(frameCycleGroupList.get(i-1));
                log.info("frameCycleGroupList = {} ", frameCycleGroupList.get(i-1));
                i++;

            }
            System.out.println("\nStarting : Thread" + number);

            value.put("frameCycleStatsList", list);
            value.putInt("minValue", start);
            value.putString("name", "partition-" + number);
            //value.putInt("maxValue", end);
            result.put("partition" + number, value);

            start += targetSize;
            end += targetSize;
            number++;
        }
        log.info("result = {}", result);
        return result;
    }
}
