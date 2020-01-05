# ab-watson-msg
IBM Watson Assistant with messenger

### Configuring Telegram
* Register in Telegram https://telegram.org/ with your phone number for example +1(514)123-4567
* Add the example chatbot @juliechatbot to the list
* Obtain api_id and api_hash https://core.telegram.org/api/obtaining_api_id
for example 391152 and 24dd4e2a02a41b5da140a2940555ce03
* Build jar and run
```
java -Dtelegram.api.id=391152 -Dtelegram.api.hash=24dd4e2a02a41b5da140a2940555ce03 \
  -Dtelegram.phoneNumber=15141234567 -jar target/ab-watson-msg.jar
```

For the first run the application will ask for an sms code, input it in console

### Testing Telegram
* Open http://localhost:8080/test/text

the response will look like this
```
<voice-transformation type="Custom" timbre="Breeze" timbre_extent="60%">
Hello, how are you today?</voice-transformation>
```
please do not make too many test requests or the chat can be blocked
* Configure port forwarding and connect to local IP https://www.google.com/search?q=what+is+my+ip

http://123.456.789.12:8080/test/text

### Configuring IBM Voice Agent with Watson
* Create an account on IBM Cloud, create Voice Agent

https://cloud.ibm.com/docs/services/voice-agent?topic=voice-agent-getting-started

### Edit agent:
* Service type - Other Watson Assistant service instance
* Credential type - User name and password
* URL - http://123.456.789.12:8080/watson
* User name - any
* Password - any
* Workspace ID - any
* Text to Speech Voice - en-US_LisaVoice: Lisa: American English female voice

### Ready to go.
Call SIP phone number, talk with Telegram bot and see lines running on the screen.
```
[nio-8080-exec-7] info.ab.WatsonAssistantController: i: hello
[nio-8080-exec-7] info.ab.WatsonAssistantController: o: Hello, how are you?
[nio-8080-exec-8] info.ab.WatsonAssistantController: i: I'm good thank you
[nio-8080-exec-8] info.ab.WatsonAssistantController: o: Pleased to meet you good thank you.
[nio-8080-exec-4] info.ab.WatsonAssistantController: i: what is your name
[nio-8080-exec-4] info.ab.WatsonAssistantController: o: My name is Julie.
```
