package demo.webflux.kata.heal.api;

public class PatientDTO {

	private Long id;

	private boolean contaminated;

	private boolean recovered;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isContaminated() {
		return contaminated;
	}

	public void setContaminated(boolean contaminated) {
		this.contaminated = contaminated;
	}

	public boolean isRecovered() {
		return recovered;
	}

	public void setRecovered(boolean recovered) {
		this.recovered = recovered;
	}

}
