package hello;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Component
public class Runner implements CommandLineRunner {
    public static double getRandomIntegerBetweenRange(double min, double max){
        double x = (int)(Math.random()*((max-min)+1))+min;
        return x;
    }
    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;
    private String server = "https://locale-apoorvanand.b542.starter-us-east-2a.openshiftapps.com/search/";
    private String server1 = "https://www.goodreads.com/book/isbn/0441172717?key=lTgEXm5uzTMM3gI8ZAw";
  private RestTemplate rest;
  private HttpHeaders headers;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
        this.rest = new RestTemplate();
    this.headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "*/*");
    }
    public String getPlaces() {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server+getRandomIntegerBetweenRange(1,100), HttpMethod.GET, requestEntity, String.class);
       // this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
      }
      public String getBooks() {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server1, HttpMethod.GET, requestEntity, String.class);
       // this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
      }

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(Application.topicExchangeName, "foo.bar.baz", "Hello from Zypher"+getPlaces()+"books recommendation is "+getBooks());
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}