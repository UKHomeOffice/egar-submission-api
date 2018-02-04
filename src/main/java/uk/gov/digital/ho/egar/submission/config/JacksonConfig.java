package uk.gov.digital.ho.egar.submission.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration of objects for use with jackson json conversion library.
 *
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates a configured object mapper.
     * Disables write dates as timestamps.
     * @param builder The Jackoson object mapper builder
     * @return The configured object mapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(final Jackson2ObjectMapperBuilder builder) {
        
    	ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // Configure JSON to return UTC dates
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        

        return objectMapper;
    }
}