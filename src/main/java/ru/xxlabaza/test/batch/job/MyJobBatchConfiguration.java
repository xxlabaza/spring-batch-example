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

import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.xxlabaza.test.batch.AppProperties;
import ru.xxlabaza.test.batch.job.MyJobBeanConfiguration.MyItemWriterToSystemOut;
import ru.xxlabaza.test.batch.model.Person;

/**
 *
 * @author Artem Labazin
 * <p>
 * @since Jan 16, 2016 | 11:27:40 PM
 * <p>
 * @version 1.0.0
 */
@Configuration
@EnableBatchProcessing
class MyJobBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JpaPagingItemReader<Person> reader;

    @Autowired
    private PersonProcessor processor;

    @Autowired
    private MyItemWriterToSystemOut writer;

    @Autowired
    private AppProperties appProperties;

    @Bean
    public Job myJob () {
        return jobs.get("my-job")
                .flow(masterStep())
                .end()
                .build();
    }

    @Bean
    public Step masterStep () {
        return stepBuilderFactory.get("master-step")
                .<Person, ThreadInfoPersonWrapper>partitioner("partitioned-step", partitioner())
                .step(partitionedStep())
                .gridSize(appProperties.getBatch().getMyJob().getPartitions())
                .taskExecutor(taskExecutor())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step partitionedStep () {
        return stepBuilderFactory.get("partitioned-step")
                .<Person, ThreadInfoPersonWrapper>chunk(appProperties.getBatch().getMyJob().getChunkSize())
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Partitioner partitioner () {
        return new RangePartitioner();
    }

    @Bean
    protected ThreadPoolTaskExecutor taskExecutor () {
        val executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("task-executor-thread-");
        executor.setCorePoolSize(appProperties.getBatch().getMyJob().getThreads());
        return executor;
    }
}
