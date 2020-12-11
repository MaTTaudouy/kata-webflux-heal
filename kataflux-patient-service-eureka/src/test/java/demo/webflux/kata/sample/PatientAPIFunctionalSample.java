package demo.webflux.kata.sample;
import demo.webflux.kata.patient.model.Patient;
import demo.webflux.kata.patient.repository.PatientRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class PatientAPIFunctionalSample {

	@Bean
	public RouterFunction<ServerResponse> routes(PatientRepository patientRepository) {
		return route()
				.GET("/patients", serverRequest -> ok()
						.body(patientRepository.findAll(), Patient.class))
				.GET("/patients/{id}",
						serverRequest -> ok().body(
								patientRepository
									.findById(Long.valueOf(serverRequest.pathVariable("id"))),
								Patient.class))
				.POST("/patients", request -> request.body(toMono(Patient.class))
						.doOnNext(patientRepository::save)
						.then(ok().build()))
				.build();
	}

}
