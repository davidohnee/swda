#
# Copyright 2024 Hochschule Luzern Informatik.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM cr.gitlab.switch.ch/hslu/shared/devops/docker-cache/amazoncorretto:21.0.4-alpine
ARG CI_GIT_VERSION
ARG CI_GIT_DATE

# add jar to image
RUN echo "$CI_GIT_VERSION,$CI_GIT_DATE" > version.txt
COPY ./target/service.jar service.jar

EXPOSE 8090/tcp
CMD java ${JAVA_OPTS} -jar ./service.jar
