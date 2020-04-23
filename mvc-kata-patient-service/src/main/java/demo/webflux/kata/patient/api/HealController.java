package demo.webflux.kata.patient.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import demo.webflux.kata.patient.model.Patient;
import demo.webflux.kata.patient.repository.PatientRepository;

@RestController
@RequestMapping("/heal")
public class HealController {

	private PatientRepository patientRepository;

	public HealController(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@PutMapping("/cure/{patientId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Patient heal(@PathVariable Long patientId) {
		final var patient = patientRepository.findById(patientId).orElseThrow();
		this.cure(patient);
		return patientRepository.save(patient);
	}

	private void cure(Patient p) {
		p.setRecovered(true);
		p.setContaminated(false);
	}

}
