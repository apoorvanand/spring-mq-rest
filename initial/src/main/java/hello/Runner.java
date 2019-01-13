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
    private String server = "https://test-zypher.herokuapp.com/onboarding/getusers/";
    private String server1 = "https://www.goodreads.com/book/isbn/0441172717?key=lTgEXm5uzTMM3gI8ZAw";
    private String server2 = "https://test-zypher.herokuapp.com/onboarding/books/";
    private String server3 = "https://api.nytimes.com/svc/books/v3/lists.json?";

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
    public String getUsers() {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server+getRandomIntegerBetweenRange(1,290), HttpMethod.GET, requestEntity, String.class);
       // this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
      }
      public String getRecommendation() {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server1, HttpMethod.GET, requestEntity, String.class);
       // this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
      }
      public String getBooks() {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server2+getRandomIntegerBetweenRange(1,290), HttpMethod.GET, requestEntity, String.class);
       // this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
      }
      public String getTopShelves() {
       final String[] Genre = {"indigenous-americans","relationships","paperback-business-books","family","hardcover-political-books","race-and-civil-rights","religion-spirituality-and-faith","science","sports","travel" };
       final Integer r=(int)Math.round(getRandomIntegerBetweenRange(0, Genre.length));
       final String Key="api-key=53c44062e6e349ffa4302aab0daa9c8a&list=";
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(server3+Key+Genre[r], HttpMethod.GET, requestEntity, String.class);
       // this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
      }
      
    @Override
    public void run(String... args) throws Exception {
        
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(Application.topicExchangeName, "foo.bar.baz", "Hello from SuperPowerBot"+getUsers()+"books recommendation is "+"getRecommendation()"+"Top Books"+getTopShelves()+"Currently Holding"+getBooks());
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}