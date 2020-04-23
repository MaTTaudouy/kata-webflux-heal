package demo.webflux.kata.patient.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;

import demo.webflux.kata.patient.model.Patient;
import demo.webflux.kata.patient.repository.PatientRepository;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/heal")
public class HealController {

	private PatientRepository patientRepository;

	public HealController(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@PutMapping("/cure/{patientId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Mono<Patient> heal(@PathVariable Long patientId) {
		return patientRepository.findById(patientId).doOnNext(this::cure).flatMap(patientRepository::save);
	}

	private void cure(Patient p) {
		p.setRecovered(true);
		p.setContaminated(false);
	}

	@CrossOrigin(value = CorsConfiguration.ALL)
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/stream")
	public Flux<Patient> streamUpdatePatient() {
		return patientRepository.findRecoveredOrContaminated().onBackpressureBuffer(10,
				BufferOverflowStrategy.DROP_OLDEST);
	}
}
