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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class VoiceSsml {

  @Value("${ab.ssml:%s}")
  private String ssmlFormat;

  /**
   * Apply random voice transformation SSML to the string. Supported by IBM Allison, Lisa and Michael voices.
   *
   * @param input input text
   * @return SSML with random voice
   */
  public String applyRandom(String input) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    return String.format("<voice-transformation type=\"Custom\" " +
        "pitch=\"%d%%\" pitch_range=\"%d%%\" glottal_tension=\"%d%%\" breathiness=\"%d%%\" " +
        "rate=\"%d%%\" timbre=\"%s\" timbre_extent=\"%d%%\">%s</voice-transformation>",
        random.nextInt(-100, 101), random.nextInt(-100, 101), random.nextInt(-100, 101), random.nextInt(-100, 101),
        random.nextInt(-100, 101), random.nextBoolean() ? "Sunrise" : "Breeze", random.nextInt(101), input);
  }

  public String apply(String text) {
    if (text.isEmpty()) {
      return text;
    }
    if (ssmlFormat == null || ssmlFormat.isEmpty()) {
      return applyRandom(text);
    }
    return String.format(ssmlFormat, text);
  }

}
