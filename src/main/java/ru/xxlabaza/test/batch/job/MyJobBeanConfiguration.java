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

import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import lombok.val;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.xxlabaza.test.batch.model.Person;

/**
 *
 * @author Artem Labazin
 * <p>
 * @since Jan 15, 2016 | 7:41:18 PM
 * <p>
 * @version 1.0.0
 */
@Configuration
class MyJobBeanConfiguration {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean(destroyMethod = "") // http://stackoverflow.com/a/23089536
    @StepScope
    public JpaPagingItemReader<Person> reader (@Value("#{stepExecutionContext[from]}") Integer from,
                                               @Value("#{stepExecutionContext[to]}") Integer to
    ) {
        val reader = new JpaPagingItemReader<Person>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setSaveState(false);

        reader.setQueryString("SELECT person " +
                              "FROM Person AS person " +
                              "WHERE person.id BETWEEN :from AND :to");
        reader.setParameterValues(new HashMap<String, Object>() {
            {
                put("from", from);
                put("to", to);
            }
        });
        return reader;
    }

    @Bean
    @StepScope
    public PersonProcessor processor () {
        return new PersonProcessor();
    }

    @Bean
    public MyItemWriterToSystemOut writer () {
        return new MyItemWriterToSystemOut();
    }

    class MyItemWriterToSystemOut extends AbstractItemStreamItemWriter<ThreadInfoPersonWrapper> {

        @Override
        public void write (List<? extends ThreadInfoPersonWrapper> items) throws Exception {
            val currentThreadName = Thread.currentThread().getName();

            System.out.println();
            items.stream().forEach(item -> {
                item.setWriterThreadName(currentThreadName);
                System.out.println(item);
            });
            System.out.println();
        }
    }
}
