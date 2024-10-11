/*
 * Copyright 2024 Roland Gisler, HSLU Informatik, Switzerland
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
package ch.hslu.swda.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller für Root-Ping.
 */
@Controller("/")
public class PingController {

    private static final Logger LOG = LoggerFactory.getLogger(PingController.class);

    /**
     * Ping auf Request.
     *
     * @return Hello.
     */
    @Tag(name = "Ping")
    @Get(value = "/", produces = "text/string")
    public String ping() {
        LOG.info("Ping");
        return "PING";
    }
}
