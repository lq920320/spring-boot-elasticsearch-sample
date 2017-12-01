package sample.data.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author liuqian
 * @date 2017/11/29 9:56.
 */
public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {
  /**
   * 通过名字搜索
   *
   * @param firstName
   * @return
   */
  List<Customer> findByFirstName(String firstName);

  /**
   * 通过姓氏搜索
   *
   * @param lastName
   * @return
   */
  List<Customer> findByLastName(String lastName);
}
