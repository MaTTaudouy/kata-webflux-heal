package demo.webflux.kata.heal;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import demo.webflux.kata.heal.api.PatientDTO;

@Configuration
public class PopulateMvcPandemie {

	private static final int RECOVERY_DELAY = 2;

	private static final int CONTAMINATION_DELAY = 1;

	private static final int VACCINATION_DELAY = 5;

	@Bean
	public ApplicationRunner virus() {
		return (args) -> {
			//FIXME 1 à 100
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<List<PatientDTO>> response = restTemplate.exchange("http://localhost:8080/patients",
					HttpMethod.GET, null, new ParameterizedTypeReference<List<PatientDTO>>() {
					});
			
			final List<PatientDTO> patients = response.getBody();
			Collections.shuffle(patients);

			patients.stream().forEach((p) -> {
				final var postClient = new RestTemplate();
				postClient.put("http://localhost:8080/contaminate/" + p.getId(), HttpEntity.EMPTY);
				try {
					Thread.sleep(CONTAMINATION_DELAY * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
			});
		};
	}

	@Bean
	public ApplicationRunner vaccination() {
		return (args) -> {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<List<PatientDTO>> response = restTemplate.exchange("http://localhost:8080/patients",
					HttpMethod.GET, null, new ParameterizedTypeReference<List<PatientDTO>>() {
					});

			final List<PatientDTO> patients = response.getBody();
			Collections.shuffle(patients);

			patients.stream().forEach((p) -> {
				final var postClient = new RestTemplate();
				postClient.put("http://localhost:8080/heal/cure/" + p.getId(), HttpEntity.EMPTY);
				try {
					Thread.sleep(VACCINATION_DELAY * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
			});

		};
	}

//	@Bean
//	public ApplicationRunner cure(WebClient webClientBase) {
//		return (args) -> {
//
//			var webClient = webClientBase.mutate().build();
//			var persons = webClient.get().uri("/heal/stream").accept(MediaType.TEXT_EVENT_STREAM).retrieve()
//					.bodyToFlux(PatientDTO.class);
//
//			persons.filter(PatientDTO::isContaminated).delayElements(Duration.ofSeconds(RECOVERY_DELAY))
//					.doOnNext((p) -> webClient.put().uri("/heal/cure/{patientId}", p.getId()).exchange().subscribe())
//					.subscribe();
//		};
//	}

}
