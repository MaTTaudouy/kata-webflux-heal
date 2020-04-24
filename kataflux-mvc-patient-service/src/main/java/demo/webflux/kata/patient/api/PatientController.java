package demo.webflux.kata.patient.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;

import demo.webflux.kata.patient.model.Patient;
import demo.webflux.kata.patient.repository.PatientRepository;

@RestController
@RequestMapping("/patients")
public class PatientController {

	private PatientRepository patientRepository;

	public PatientController(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@CrossOrigin(CorsConfiguration.ALL)
	@GetMapping
	public List<Patient> getAll() {
		return patientRepository.findAll();
	}

	@GetMapping("/{id}")
	public Patient getById(@PathVariable Long id) {
		return patientRepository.findById(id).orElseThrow();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Patient save(@RequestBody Patient entity) {
		return patientRepository.save(entity);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		patientRepository.deleteById(id);
	}

}
