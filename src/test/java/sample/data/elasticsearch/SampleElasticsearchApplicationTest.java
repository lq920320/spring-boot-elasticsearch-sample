package sample.data.elasticsearch;

import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.rule.OutputCapture;
import sample.SampleElasticsearchApplication;

import java.io.File;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author liuqian
 * @date 2017/11/29 9:58.
 */
public class SampleElasticsearchApplicationTest {
  @Rule
  public OutputCapture outputCapture = new OutputCapture();

  @ClassRule
  public static SkipOnWindows skipOnWindows = new SkipOnWindows();

  @Test
  public void testDefaultSettings() throws Exception {
    try {
      new SpringApplicationBuilder(SampleElasticsearchApplication.class).run();
    }
    catch (Exception ex) {
      if (!elasticsearchRunning(ex)) {
        return;
      }
      throw ex;
    }
    String output = this.outputCapture.toString();
    assertThat(output).contains("firstName='Alice', lastName='Smith'");
  }

  private boolean elasticsearchRunning(Exception ex) {
    Throwable candidate = ex;
    while (candidate != null) {
      if (candidate instanceof NoNodeAvailableException) {
        return false;
      }
      candidate = candidate.getCause();
    }
    return true;
  }

  static class SkipOnWindows implements TestRule {

    @Override
    public Statement apply(final Statement base, Description description) {
      return new Statement() {

        @Override
        public void evaluate() throws Throwable {
          if (!runningOnWindows()) {
            base.evaluate();
          }
        }

        private boolean runningOnWindows() {
          return File.separatorChar == '\\';
        }

      };
    }

  }

}