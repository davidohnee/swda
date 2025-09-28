/*
 * Copyright 2024 Roland Gisler, Hochschule Luzern - Informatik.
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
package ch.hslu.swda.micronaut;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;

/**
 * Testcases für Student Service. Verwendet Micronaut.io.
 */
final class EndpointStudentTest {

    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeAll
    static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server.getApplicationContext().createBean(HttpClient.class, server.getURL());
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    public void testGetStudentOne() throws Exception {
        final HttpRequest<String> request = HttpRequest.GET("/api/v1/students/1");
        final String body = client.toBlocking().retrieve(request);
        Assertions.assertThat(body).contains("\"lastName\":\"Grone\"");
    }

    @Test
    public void testGetStudentTwo() throws Exception {
        final HttpRequest<String> request = HttpRequest.GET("/api/v1/students/2");
        final String body = client.toBlocking().retrieve(request);
        Assertions.assertThat(body).contains("\"lastName\":\"Zweifel\"");
    }

    @Test
    public void testStudentSearch() throws Exception {
        final HttpRequest<String> request = HttpRequest.GET("/api/v1/students?lastname=Zweifel");
        final String body = client.toBlocking().retrieve(request);
        Assertions.assertThat(body).contains("\"lastName\":\"Zweifel\"");
    }
}
