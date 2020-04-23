package demo.webflux.kata.patient;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.javafaker.Faker;

import demo.webflux.kata.patient.model.Patient;
import demo.webflux.kata.patient.repository.PatientRepository;

@Configuration
public class PopulateH2Data {

	@Bean
	public ApplicationRunner runner(PatientRepository patientRepository, DataSource db) {
		return (args) -> {
			try (final var prepareStatement = db.getConnection()
					.prepareStatement("CREATE TABLE IF NOT EXISTS patient (" + "id SERIAL PRIMARY KEY,"
							+ "first_name VARCHAR(255) NOT NULL," + "last_name VARCHAR(255) NOT NULL,"
							+ "contaminated boolean DEFAULT false," + "recovered boolean DEFAULT false" + ");")) {

				prepareStatement.execute();
				Faker faker = new Faker(Locale.getDefault());
				Stream<Patient> patients = Stream
						.generate(() -> new Patient(faker.name().firstName(), faker.name().lastName())).limit(100);

				patientRepository.saveAll(patients.collect(Collectors.toList()));
			}
		};
	}
}
