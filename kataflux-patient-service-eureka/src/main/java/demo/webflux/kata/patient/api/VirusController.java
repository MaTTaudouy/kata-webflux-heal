package demo.webflux.kata.patient.api;

import java.util.function.Predicate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import demo.webflux.kata.patient.model.Patient;
import demo.webflux.kata.patient.repository.PatientRepository;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/contaminate")
public class VirusController {

	private PatientRepository patientRepository;

	public VirusController(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@PutMapping("/{patientId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Mono<Patient> contaminate(@PathVariable Long patientId) {
		return patientRepository.findById(patientId).filter(Predicate.not(Patient::isRecovered))
				.doOnNext((p) -> p.setContaminated(true)).flatMap(patientRepository::save);
	}

}
