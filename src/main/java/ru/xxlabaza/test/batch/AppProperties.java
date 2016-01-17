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
package ru.xxlabaza.test.batch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author Artem Labazin
 * <p>
 * @since Jan 17, 2016 | 1:54:43 PM
 * <p>
 * @version 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Batch batch;

    @Data
    public static class Batch {

        private MyJob myJob;

        @Data
        public static class MyJob {

            private Integer threads;

            private Integer chunkSize;

            private Integer partitions;
        }
    }
}