package demo.webflux.kata.patient.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import demo.webflux.kata.patient.model.Patient;

public interface PatientRepository extends CrudRepository<Patient, Long> {

	@Query("SELECT * FROM patient where contaminated = true or recovered = true")
	public List<Patient> findRecoveredOrContaminated();

	public List<Patient> findAll();

}
