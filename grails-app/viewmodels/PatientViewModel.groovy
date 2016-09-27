import org.apache.http.HttpResponse
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.select.annotation.Wire
import ca.uhn.fhir.model.dstu2.resource.*
import ca.uhn.fhir.model.dstu2.composite.*
import ca.uhn.fhir.context.*
import ca.uhn.fhir.rest.client.*
import ca.uhn.fhir.model.dstu2.valueset.*
import ca.uhn.fhir.model.primitive.*
import ca.uhn.fhir.rest.client.interceptor.*



class PatientViewModel {

    String message
    @Wire  btnHello

    @Init init() {
        // initialzation code here
    }

    @NotifyChange(['message'])
    @Command clickMe() {
        message = "Clicked"
        // Create a patient object
        Patient patient = new Patient();
        patient.addIdentifier()
                .setSystem("http://acme.org/mrns")
                .setValue("12345");
        patient.addName()
                .addFamily("Jameson")
                .addGiven("J")
                .addGiven("Jonah");
        patient.setGender(AdministrativeGenderEnum.MALE);

// Give the patient a temporary UUID so that other resources in
// the transaction can refer to it
        patient.setId(IdDt.newRandomUuid());

// Create an observation object
        Observation observation = new Observation();
        observation.setStatus(ObservationStatusEnum.FINAL);
        observation
                .getCode()
                .addCoding()
                .setSystem("http://loinc.org")
                .setCode("789-8")
                .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
        observation.setValue(
                new QuantityDt()
                        .setValue(4.12)
                        .setUnit("10 trillion/L")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("10*12/L"));

// The observation refers to the patient using the ID, which is already
// set to a temporary UUID
        observation.setSubject(new ResourceReferenceDt(patient.getId().getValue()));

// Create a bundle that will be used as a transaction
        Bundle bundle = new Bundle();
        bundle.setType(BundleTypeEnum.TRANSACTION);

// Add the patient as an entry. This entry is a POST with an
// If-None-Exist header (conditional create) meaning that it
// will only be created if there isn't already a Patient with
// the identifier 12345
        bundle.addEntry()
                .setFullUrl(patient.getId().getValue())
                .setResource(patient)
                .getRequest()
                .setUrl("Patient")
                .setIfNoneExist("Patient?identifier=http://acme.org/mrns|12345")
                .setMethod(HTTPVerbEnum.POST);

// Add the observation. This entry is a POST with no header
// (normal create) meaning that it will be created even if
// a similar resource already exists.

// Log the request
        FhirContext ctx = FhirContext.forDstu2();
        bundle.addEntry()
                .setResource(observation)
                .getRequest()
                .setUrl("Observation")
                .setMethod(HTTPVerbEnum.POST);
        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));

/*
        String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6InJzYTEifQ.eyJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3Q6ODA4MFwvb3BlbmlkLWNvbm5lY3Qtc2VydmVyLXdlYmFwcFwvIiwiYXVkIjoiYjAzMmRmNzAtOGNmNS00NDNmLTliNWMtNjk4MWMxZjk1ZWMyIiwianRpIjoiNzBkMTY4YzItNzY3YS00ZGYxLWJkYjUtNzk0YWU1YThkNjQwIiwiaWF0IjoxNDY4ODgyNjQwfQ.O31TU5LEQdU8m1HlJuoe3JwSyDGGcqNiLii_v8UhaV3Eq0dqGGA1F8l14qaQQHBhYpdagVOdIFa7m1NpnO0xatFLR7rPD0-EDWaNSDn_s20bqbv0-yuGCRmpy9AA3H3l6T4X0n7tcsjHE2UvTU3OGQ02kb83QyhTedUZ4gvN5kF0bxtb-aqxFjLHj0jJSOCn7WCRuC0wCaCNqncyeEx39-Z4zxmYjrZLg0xMVm2efPMiKeA603pLxNUYJngQwHjx00NnNYTC06S76RsArod1_TVmcFLohuzfgcMSlCik4MnV4qh5jV2IE2DyqeFCDzKudvt0RCzjkDBuQhuUmYq9lQ";
        BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(token);
*/
//        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();

// Optionally you may configure the interceptor (by default only
// summary info is logged)
//        loggingInterceptor.setLogRequestSummary(true);
 //       loggingInterceptor.setLogRequestBody(true);
// Create a client and post the transaction to the server
        String username = "user";
        String password = "password";
        BasicAuthInterceptor authInterceptor = new BasicAuthInterceptor(username, password);
        IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-fhir-jpaserver-example/baseDstu2");
        client.registerInterceptor(authInterceptor)
//        HttpResponse theResponse = authInterceptor.interceptResponse()
        //client.logRequestAndResponse = true
        Bundle resp = new Bundle()

        try {
           resp = client.transaction().withBundle(bundle).execute();

        } catch(ex) {
            println(ex.message)
        };
//        HttpResponse theResponce = loggingInterceptor.interceptResponse()
// Log the response
//        System.out.println(theResponse);
        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));
        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(patient));
    }

}
