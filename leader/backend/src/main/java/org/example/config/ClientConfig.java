package org.example.config;

import static java.util.Objects.isNull;
import static org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY;

import java.util.function.Function;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

@Configuration
public class ClientConfig {

  @Bean
  Function<String, RestClient> restClientFactory(RestClient.Builder builder) {
    return server -> newRestClient(server, builder);
  }

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  RestClient newRestClient(String server, RestClient.Builder builder) {
    return builder.uriBuilderFactory(getDefaultHandler(server)).build();
  }

  private UriBuilderFactory getDefaultHandler(String baseUri) {
    final var uriTemplateHandler =
        isNull(baseUri) ? new DefaultUriBuilderFactory() : new DefaultUriBuilderFactory(baseUri);
    uriTemplateHandler.setEncodingMode(VALUES_ONLY);
    return uriTemplateHandler;
  }
}
