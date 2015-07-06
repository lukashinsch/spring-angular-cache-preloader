package eu.hinsch.spring.angular.cache;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lukas.hinsch on 06.07.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebIntegrationTest(randomPort = true)
public class CachePreloadingIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldIncludeCachedResultInRespons() throws Exception {
        // given
        final GetMethod method = new GetMethod("http://localhost:" + port + "/test/index.html");

        // when
        int status = new HttpClient().executeMethod(method);

        // then
        assertThat(status, is(200));
        final String body = new String(method.getResponseBody());
        assertThat(body, containsString("httpCache.put('api/simple-list', '[\"One\",\"Two\",\"Three\"]');"));
    }
}
