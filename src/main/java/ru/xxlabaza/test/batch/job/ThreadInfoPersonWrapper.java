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
import lombok.Data;

/**
 *
 * @author Artem Labazin
 * <p>
 * @since Jan 16, 2016 | 10:59:08 PM
 * <p>
 * @version 1.0.0
 */
@Data
class ThreadInfoPersonWrapper {

    private final Person person;

    private final String executionContextName;

    private final String processorThreadName;

    private String writerThreadName;

    @Override
    public String toString () {
        return new StringBuilder()
                .append("EXECUTION CONTEXT NAME: ").append(executionContextName).append('\n')
                .append("PROCESSOR THREAD NAME:  ").append(processorThreadName).append('\n')
                .append("WRITER THREAD NAME:     ").append(writerThreadName).append('\n')
                .append("OBJECT:                 ").append(person)
                .toString();
    }
}
