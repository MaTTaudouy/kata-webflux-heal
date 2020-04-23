package demo.webflux.kata.patient.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import demo.webflux.kata.patient.model.Patient;
import reactor.core.publisher.Flux;

public interface PatientRepository extends R2dbcRepository<Patient, Long> {

	@Query("SELECT * FROM patient where contaminated = true or recovered = true")
	public Flux<Patient> findRecoveredOrContaminated();
}
