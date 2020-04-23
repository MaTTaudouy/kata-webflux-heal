# Instructions Coding Dojo Spring webflux

## Installation
* Récupérer code mvc
* Récupérer l'appli React
* Récupérer la correction
* Faire les mvn install
* Faire le npm install

## Initialisation
* Explorer le code des applis
* Démarrer le serveur patient-service
* Démarrer l'appli web
* Démarrer le heal-service
* Faire F5 de manière régulière pour voir les changements en bdd

## Migration de WebMvc vers WebFlux
### Utilisation de l'API Reactor
* Dans HealController & VirusController
* Modifier les List en FLux
* Modifier les Objet en Mono

### Réduire la complexité inutile
* __Chasse au code complexe inutile__ (Utilisation de l'API reactor)
* Optimiser la lambda demo.webflux.kata.patient.api.HealController.heal(Long) en 1 ligne
* Optimiser la lambda	demo.webflux.kata.patient.api.VirusController.contaminate(Long) en 1 ligne
* ON peut maintenant vérifier que l'application continue de fonctionnaermarché.
__/!\  Nous n'utilisons toujours pas de ServerSentEvent et le R2DBC__

## Migration de la BDD

* Modifier le PatientRepository pour qu'il hérite d'un R2dbcRepository
* Modifier le fichier application.yaml
```Java
spring:
   r2dbc:
	  url: r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
	  name: sa
	  password: null
	  console:
		 enabled: true
```

* Dans HealController
```Java
@CrossOrigin(value = CorsConfiguration.ALL)
@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/stream")
public Flux<Patient> streamUpdatePatient() {
	return patientRepository.findRecoveredOrContaminated().onBackpressureBuffer(10,
			BufferOverflowStrategy.DROP_OLDEST);
}
```
## Rendre les clients  réactif
### Rendre le client javascript réactif
* On fait le code client pour l'EventSource
```Javascript
let eventSource = new EventSource("http://localhost:8080/heal/stream");	
eventSource.onmessage = e => updatePatientList(JSON.parse(e.data));
```

### Rendre le client Java réactif
* Migration RestTemplate vers Web client__Seulement si on a le temps (30min à 1h)__ Sinon on remplace le projet mvc-heal-service par le webflux-heal-service
* Comment envoyer des données de manière réactive
```Java
#Exemple d'utilisation de la fonction delay
Flux.fromIterable(personsList)
	.delayElements(Duration.ofSeconds(VACCINATION_DELAY))
	.doOnNext((p) -> webClient.put().uri("/heal/cure/{patientId}", p.getId()).exchange().subscribe())
	.subscribe();
#Double subscribe car le webClient est lui même un Publisher
```
* Comment récupérer des données de manière réactive (demo.webflux.kata.heal.PopulatePandemie.cure(WebClient))
```Java
#Ici le client réactif Java écoute le "streamUpdatePatient"
webClient.get()
	.uri("/heal/stream").accept(MediaType.TEXT_EVENT_STREAM).retrieve()
	.bodyToFlux(PatientDTO.class)
	.filter(PatientDTO::isContaminated)
	.delayElements(Duration.ofSeconds(RECOVERY_DELAY))
	.subscribe((p) -> webClient.put().uri("/heal/cure/{patientId}", p.getId())
		.exchange()
		.subscribe());
```

## Profiter du temps réel
* Démarrer le server patient-service
* Démarrer l'appli web
* Démarrer le pandemie
* Plus besoin de faire F5. Le client web écoute les événements sans faire de requête
	
