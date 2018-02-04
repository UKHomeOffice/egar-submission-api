package uk.gov.digital.ho.egar.submission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import uk.gov.digital.ho.egar.constants.SwaggerConstants;
import uk.gov.digital.ho.egar.flyway.FlywayRunner;
import uk.gov.digital.ho.egar.flyway.FlywayRunnerArgs;
import uk.gov.digital.ho.egar.flyway.NoMigrationStatagy;

@SpringBootApplication
@ComponentScan( basePackageClasses= {SubmissionApplication.class} ,
		basePackages = { "uk.gov.digital.ho.egar.shared.util.monitoring" })
public class SubmissionApplication {

	public static void main(String[] args) {
	    /*
	     * Checks if there is a commandline flyway argument, and if so runs with flyway
	     */
	    if ( FlywayRunnerArgs.hasFlywayArg(args) ){
	        FlywayRunner.main(args);
	    }else
	        SpringApplication.run(SubmissionApplication.class, args);
	     
	}
	 
	/*
	 * Changes the default state of flyway to disabled
	 */
	@Bean
	public FlywayMigrationStrategy disableFlywayMigrationStrategy()
	{
	    return new NoMigrationStatagy();
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern(SwaggerConstants.PATH_RESOURCES_REGEX)) {
			registry
					.addResourceHandler(SwaggerConstants.PATH_RESOURCES_REGEX)
					.addResourceLocations(SwaggerConstants.PATH_RESOURCES_META_INF);
		}

	}
}
