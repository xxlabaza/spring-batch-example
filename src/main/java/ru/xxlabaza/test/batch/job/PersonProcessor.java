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

import ru.xxlabaza.test.batch.model.Person;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Artem Labazin
 * <p>
 * @since Jan 16, 2016 | 1:02:18 AM
 * <p>
 * @version 1.0.0
 */
class PersonProcessor implements ItemProcessor<Person, ThreadInfoPersonWrapper> {

    @Value("#{stepExecutionContext[name]}")
    private String name;

    @Override
    public ThreadInfoPersonWrapper process (Person person) throws Exception {
        return new ThreadInfoPersonWrapper(person, name, Thread.currentThread().getName());
    }
}
