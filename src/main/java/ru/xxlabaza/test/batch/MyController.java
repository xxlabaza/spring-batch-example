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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 *
 * @author Artem Labazin
 * <p>
 * @since Jan 17, 2016 | 3:53:58 AM
 * <p>
 * @version 1.0.0
 */
@RestController
class MyController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job myJob;

    @RequestMapping
    @ResponseStatus(NO_CONTENT)
    public void runJob () throws Exception {
        jobLauncher.run(myJob, new JobParameters());
    }
}
