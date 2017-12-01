package sample.data.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author liuqian
 * @date 2017/11/29 9:53.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "customer", type = "customer", shards = 1, replicas = 0, refreshInterval = "-1")
public class Customer {
  @Id
  private String id;

  private String firstName;

  private String lastName;

  public Customer(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    return String.format("Customer[id=%s, firstName='%s', lastName='%s']", this.id,
      this.firstName, this.lastName);
  }

}
