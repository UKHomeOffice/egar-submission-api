package uk.gov.digital.ho.egar.submission.config;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import uk.gov.digital.ho.egar.shared.auth.api.filter.config.FilterConfig;

@Configuration
@ComponentScan(basePackages = {"uk.gov.digital.ho.egar.shared.util, uk.gov.digital.ho.egar.shared.auth"})
public class ApplicationConfig {
    private  static final String API_V1_PATTERN = "\\/api\\/v1*.*";
    protected final Log logger = LogFactory.getLog(getClass());

    @Bean
    public FilterConfig filterConfig() {
        logger.debug("Initialising filter config");
        FilterConfig config = new FilterConfig();
        config.addPaths(Arrays.asList(
                new RegexRequestMatcher(API_V1_PATTERN, HttpMethod.POST.toString()),
                new RegexRequestMatcher(API_V1_PATTERN, HttpMethod.GET.toString()),
                new RegexRequestMatcher(API_V1_PATTERN, HttpMethod.DELETE.toString())));
        return config;
    }

}