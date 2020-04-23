package demo.webflux.kata.patient;

import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DatabaseClient;

import com.github.javafaker.Faker;

import demo.webflux.kata.patient.model.Patient;
import demo.webflux.kata.patient.repository.PatientRepository;
import reactor.core.publisher.Flux;

@Configuration
public class PopulateData {

	@Bean
	public ApplicationRunner runner(PatientRepository patientRepository, DatabaseClient db) {
		return (args) -> {
			var initDb = db.execute("" + "CREATE TABLE IF NOT EXISTS patient (" + "id SERIAL PRIMARY KEY,"
					+ "first_name VARCHAR(255) NOT NULL," + "last_name VARCHAR(255) NOT NULL,"
					+ "contaminated boolean DEFAULT false," + "recovered boolean DEFAULT false" + ");");

			Faker faker = new Faker(Locale.getDefault());
			Stream<Patient> patients = Stream
					.generate(() -> new Patient(faker.name().firstName(), faker.name().lastName())).limit(100);

			var saveAll = patientRepository.saveAll(Flux.fromStream(patients));

			initDb // initialize the database
					.then().thenMany(saveAll) // then save our Sample Patient
					.subscribe(); // execute
		};
	}
}
