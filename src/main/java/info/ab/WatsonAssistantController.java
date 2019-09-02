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

import com.ibm.cloud.sdk.core.util.GsonSingleton;
import com.ibm.watson.assistant.v1.model.Context;
import com.ibm.watson.assistant.v1.model.DialogRuntimeResponseGeneric;
import com.ibm.watson.assistant.v1.model.MessageRequest;
import com.ibm.watson.assistant.v1.model.OutputData;
import com.ibm.watson.assistant.v1.model.SystemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class WatsonAssistantController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WatsonAssistantController.class);

  private static final String CONVERSATION_ID = UUID.randomUUID().toString();

  public static final Set<String> EMPTY_INPUT = Stream.of("", "vgwHangUp", "vgwPostResponseTimeout").collect(Collectors.toSet());

  @Autowired
  private VoiceSsml voiceSsml;

  @Autowired
  private TalkService talkService;

  @PostMapping("/watson/v1/workspaces/{workspaceId}/message")
  public String message(@RequestBody String request) {

    LOGGER.debug(request);
    // Watson MessageRequest is incompatible with Jackson so there is a GsonSingleton with custom type adapters
    MessageRequest response = GsonSingleton.getGsonWithoutPrettyPrinting().fromJson(request, MessageRequest.class);

    // get input text
    String inputText = response.getInput().getText();
    if (!inputText.isEmpty()) {
      LOGGER.info("i: {}", inputText);
    }
    if (EMPTY_INPUT.contains(inputText)) {
      inputText = "";
    }

    // perform a text conversation with AI
    String outputText = talkService.talk(inputText);
    if (!outputText.isEmpty()) {
      LOGGER.info("o: {}", outputText);
    }

    // apply ssml with expression, timbre, whatever
    outputText = voiceSsml.apply(outputText);

    // create output
    final OutputData output = new OutputData();
    final DialogRuntimeResponseGeneric generic = new DialogRuntimeResponseGeneric();
    generic.setResponseType(DialogRuntimeResponseGeneric.ResponseType.TEXT);
    generic.setText(outputText);
    output.setGeneric(Collections.singletonList(generic));
    output.setText(Collections.singletonList(outputText));

    output.setNodesVisited(Collections.singletonList("Introduction"));
    output.setLogMessages(Collections.emptyList());
    response.setOutput(output);

    // update context
    final Context context = response.getContext();
    context.setSystem(new SystemResponse());
    context.setConversationId(CONVERSATION_ID);
    response.setContext(context);

    response.setIntents(Collections.emptyList());
    response.setEntities(Collections.emptyList());

    // Watson MessageRequest GsonSingleton workaround
    return GsonSingleton.getGson().toJsonTree(response).getAsJsonObject().toString();
  }

  @GetMapping(value = "/test/text", produces = "text/plain")
  public String testText() {
    return voiceSsml.apply(talkService.sayNonsense());
  }

  @GetMapping("/authorization/api/v1/token")
  public String token() {
    return "X-Watson-Authorization-Token";
  }

}
