/*
 * Copyright 2019 Aleksei Balan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.ab;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@Configuration
public class RandomTalker {

  @ConditionalOnMissingBean
  @Bean
  public Talker randomTalkerImplementation() {
    return new RandomTalkerImplementation();
  }

  public static class RandomTalkerImplementation implements Talker {

    private final Queue<String> textQueue = new LinkedList<>();

    private final RestTemplate restTemplate = new RestTemplate();

    public String getRandomText() {
      if (textQueue.isEmpty()) {
        final String s = restTemplate.getForObject("https://randomtextgenerator.com/", String.class);
        final int textBegin = s.indexOf('>', s.indexOf("<textarea")) + 1;
        final int textEnd = s.indexOf('<', textBegin);
        textQueue.addAll(Arrays.asList(s.substring(textBegin, textEnd).trim().split("[\n\r]+")));
      }
      return textQueue.remove(); // do not want poll() because of its nulls
    }

    @Override
    public String talk(String input) {
      return getRandomText();
    }

  }
}
