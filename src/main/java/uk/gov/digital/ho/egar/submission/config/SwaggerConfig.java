package uk.gov.digital.ho.egar.submission.config;

import static springfox.documentation.builders.PathSelectors.regex;

import java.time.LocalDate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.common.base.Predicate;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger1.annotations.EnableSwagger;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.digital.ho.egar.constants.SwaggerConstants;
import uk.gov.digital.ho.egar.environment.profiles.NotProdEnvironmentProfile;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionResponse;
import uk.gov.digital.ho.egar.submission.model.SubmittedGar;
import uk.gov.digital.ho.egar.submission.model.rest.GarSubmissionResponsePojo;
import uk.gov.digital.ho.egar.submission.service.repository.model.SubmittedGarPersistedRecord;

/**
 * Configure Swagger 2 for use with this project.
 *
 * @see https://dzone.com/articles/spring-boot-swagger-ui
 */
@Configuration
@EnableSwagger  //Enable swagger 1.2 spec
@EnableSwagger2 //Enable swagger 2.0 spec
@NotProdEnvironmentProfile
public class SwaggerConfig extends WebMvcConfigurerAdapter {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(apiPaths())
                .build()
                .directModelSubstitute(LocalDate.class, String.class) //Substitutes all the LocalDate objects in the JSON POJOS with a String
                .directModelSubstitute(GarSubmissionResponse.class, GarSubmissionResponsePojo.class)
                .directModelSubstitute(SubmittedGar.class, SubmittedGarPersistedRecord.class)
                .apiInfo(apiInfo());
    }

    private Predicate<String> apiPaths() {
        return regex(SwaggerConstants.PATH_DATA + "/.*");
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Civica Sample API",                       // title
                "API for Civica Sample",                  // description
                "0.0",                                 // version
                "Civica Sample API terms of service",      // termsOfServiceUrl
                new Contact("",null,null),     // contact
                "API Licence",                          // license
                "/licence.html");                       // licenseUrl


        return apiInfo;
    }
}