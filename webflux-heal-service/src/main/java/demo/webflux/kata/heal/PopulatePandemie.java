package demo.webflux.kata.heal;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import demo.webflux.kata.heal.api.PatientDTO;
import reactor.core.publisher.Flux;

@Configuration
public class PopulatePandemie {

	private static final int RECOVERY_DELAY = 2;

	private static final int CONTAMINATION_DELAY = 1;

	private static final int VACCINATION_DELAY = 5;

	@Bean
	public ApplicationRunner virus(WebClient webClientBase) {
		return (args) -> {
			// FIXME 1 à 100
			var webClient = webClientBase.mutate().build();
			var persons = webClient.get().uri("/patients").accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToFlux(PatientDTO.class);
			var personsList = persons.toStream().collect(Collectors.toList());
			Collections.shuffle(personsList);

			Flux.fromIterable(personsList).delayElements(Duration.ofSeconds(CONTAMINATION_DELAY))
					.doOnNext((p) -> webClient.put().uri("/contaminate/{patientId}", p.getId()).exchange().subscribe())
					.subscribe();
		};
	}

	@Bean
	public ApplicationRunner vaccination(WebClient webClientBase) {
		return (args) -> {

			var webClient = webClientBase.mutate().build();
			var persons = webClient.get().uri("/patients").accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToFlux(PatientDTO.class);

			var personsList = persons.toStream().collect(Collectors.toList());
			Collections.shuffle(personsList);

			Flux.fromIterable(personsList).delayElements(Duration.ofSeconds(VACCINATION_DELAY))
					.doOnNext((p) -> webClient.put().uri("/heal/cure/{patientId}", p.getId()).exchange().subscribe())
					.subscribe();
		};
	}

	@Bean
	public ApplicationRunner cure(WebClient webClientBase) {
		return (args) -> {

			var webClient = webClientBase.mutate().build();
			webClient.get().uri("/heal/stream").accept(MediaType.TEXT_EVENT_STREAM).retrieve()
					.bodyToFlux(PatientDTO.class).filter(PatientDTO::isContaminated)
					.delayElements(Duration.ofSeconds(RECOVERY_DELAY))
					.doOnNext((p) -> webClient.put().uri("/heal/cure/{patientId}", p.getId()).exchange().subscribe())
					.subscribe();
		};
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder().defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.baseUrl("http://localhost:8080").build();
	}
}
