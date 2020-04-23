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
@RequestMapping("/contaminate")
public class VirusController {

	private PatientRepository patientRepository;

	public VirusController(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@PutMapping("/{patientId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Patient contaminate(@PathVariable Long patientId) {
		final var patient = patientRepository.findById(patientId).orElseThrow();
		if (!patient.isRecovered()) {
			patient.setContaminated(true);
			return patientRepository.save(patient);
		}
		return null;
	}

}
