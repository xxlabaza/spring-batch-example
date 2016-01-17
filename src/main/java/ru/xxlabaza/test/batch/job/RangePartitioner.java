/* 
 * Copyright 2016 xxlabaza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.xxlabaza.test.batch.job;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.xxlabaza.test.batch.model.PersonRepository;

import static java.util.stream.Collectors.toMap;

/**
 *
 * @author Artem Labazin
 * <p>
 * @since Jan 16, 2016 | 8:56:27 PM
 * <p>
 * @version 1.0.0
 */
class RangePartitioner implements Partitioner {

    @Autowired
    private PersonRepository personRepository;

    @Value("${app.batch.myJob.chunkSize}")
    private int chunkSize;

    @Override
    public Map<String, ExecutionContext> partition (int gridSize) {
        long totalItems = personRepository.count();
        System.out.println("\nTotal items: " + totalItems);

        int range = (int) totalItems / gridSize;
        if (range < chunkSize) {
            throw new IllegalArgumentException();
        }

        return IntStream.range(0, gridSize).boxed()
                .map(index -> {
                    ExecutionContext context = new ExecutionContext();
                    context.putString("name", "partition-" + index);
                    context.putInt("from", index * range);
                    int nextIndex = index + 1;
                    int to = nextIndex * range - 1;
                    if (nextIndex == gridSize) {
                        to += totalItems % gridSize;
                    }
                    context.putInt("to", to);
                    return context;
                })
                .map(context -> {
                    System.out.format("\nCREATED PARTITION: '%s', RANGE FROM %d, TO %d\n",
                                      context.getString("name"),
                                      context.getInt("from"),
                                      context.getInt("to"));
                    return context;
                })
                .collect(toMap(context -> context.getString("name"), Function.identity()));
    }
}
