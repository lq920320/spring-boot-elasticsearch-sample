package sample;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import sample.data.elasticsearch.Customer;
import sample.data.elasticsearch.CustomerRepository;

/**
 * @author liuqian
 * @date 2017/11/29 9:56.
 * 添加 EnableElasticsearchRepositories 注解之后，需要elasticsearch服务开启，
 * 而不加该注解，读取的是本地data文件夹下中的文档保存的内容
 */
@SpringBootApplication
@EnableElasticsearchRepositories
public class SampleElasticsearchApplication implements CommandLineRunner {

  private final CustomerRepository repository;

  @Autowired
  public SampleElasticsearchApplication(CustomerRepository repository) {
    this.repository = repository;
  }

  @Override
  public void run(String... args) throws Exception {
    this.repository.deleteAll();
    saveCustomers();
    fetchAllCustomers();
    fetchIndividualCustomers();
  }

  private void saveCustomers() {
    this.repository.save(new Customer("Alice", "Smith"));
    this.repository.save(new Customer("Bob", "Smith"));
    this.repository.save(new Customer("Alien", "Smith"));
    this.repository.save(new Customer("Bob", "Jackson"));
    this.repository.save(new Customer("Alice", "Lee"));
    this.repository.save(new Customer("钱阿斯端上看的来上的吧", "刘手动加UI沙嗲无诶是卡拉斯科"));
    this.repository.save(new Customer("阿钱上看的来斯上的端吧", "哈哈哈哈哈"));
    this.repository.save(new Customer("钱阿斯端上看的来上的吧", "嘻嘻嘻嘻"));
    this.repository.save(new Customer("钱上阿看的斯端来上的吧", "嘻嘻嘻嘻"));
    this.repository.save(new Customer("钱上阿看的斯端来上的吧", "刘嘻嘻嘻嘻"));
  }

  private void fetchAllCustomers() {
    System.out.println("Customers found with findAll():");
    System.out.println("-------------------------------");
    for (Customer customer : this.repository.findAll()) {
      System.out.println(customer);
    }
    System.out.println();
  }

  private void fetchIndividualCustomers() {

    System.out.println("Customer found with findByFirstName('Alice'):");
    System.out.println("--------------------------------");
    for (Customer customer : this.repository.findByFirstName("Alice")) {
      System.out.println(customer);
    }
    System.out.println();
    System.out.println("----------------------------------------------");
    System.out.println("Customers found with findByLastName('Smith'):");
    System.out.println("--------------------------------");
    for (Customer customer : this.repository.findByLastName("Smith")) {
      System.out.println(customer);
    }
    System.out.println();
    System.out.println("Search pageable: findAll");
    System.out.println("--------------------------------");
    Pageable pageable = new PageRequest(0, 5);
    for (Customer customer : this.repository.findAll(pageable)) {
      System.out.println(customer);
    }
    String scoreModeSum = "sum";
    Float minScore = 10.0F;
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    Customer contentSearch = new Customer("阿斯端", "刘");
    if (contentSearch.getFirstName() != null) {
      boolQuery.must(QueryBuilders.matchPhraseQuery("firstName", contentSearch.getFirstName()));
    }
    if (contentSearch.getLastName() != null) {
      boolQuery.must(QueryBuilders.matchPhraseQuery("lastName", contentSearch.getLastName()));
    }
    FunctionScoreQueryBuilder scoreBuilder = QueryBuilders.functionScoreQuery();
    if (contentSearch.getFirstName() != null) {
      scoreBuilder.add(QueryBuilders.matchPhraseQuery("firstName", contentSearch.getFirstName()),
        ScoreFunctionBuilders.weightFactorFunction(1000)).setMinScore(minScore);
    }
    if (contentSearch.getLastName() != null) {
      scoreBuilder.add(QueryBuilders.matchPhraseQuery("lastName", contentSearch.getLastName()),
        ScoreFunctionBuilders.weightFactorFunction(500)).setMinScore(minScore);
    }
    FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(scoreBuilder)
      .scoreMode(scoreModeSum);
    Page<Customer> customerPage = this.repository.search(new NativeSearchQueryBuilder()
      .withPageable(pageable)
      .withQuery(functionScoreQueryBuilder).build());
    System.out.println(customerPage.getTotalPages());
    System.out.println(customerPage.getTotalElements());
    for (Customer customer : customerPage) {
      System.out.println(customer);
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Hello world !");
    SpringApplication.run(SampleElasticsearchApplication.class, args);
  }

}
