package fhir.client

import ca.uhn.fhir.model.dstu2.resource.Patient
import ca.uhn.fhir.model.dstu2.resource.*
import ca.uhn.fhir.model.dstu2.composite.*
import ca.uhn.fhir.context.*
import ca.uhn.fhir.rest.client.*
import ca.uhn.fhir.model.dstu2.valueset.*
import ca.uhn.fhir.model.primitive.*
import ca.uhn.fhir.rest.client.interceptor.*

class NewzulComposer extends zk.grails.Composer {





    def beforeCompose = {window ->


    }
    def afterCompose = { window ->

        FhirContext ctx = FhirContext.forDstu2();
        IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-fhir-jpaserver-example/baseDstu2");
        Bundle results = client
                .search()
                .forResource(Patient.class)
                .where(Patient.FAMILY.matches().value(""))
                .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                .execute();
        Patient[] patients = []
        patients = results.getAllPopulatedChildElementsOfType(Patient)

        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(results));
        patients.each {p ->
            System.out.println(p.getGender())
            System.out.println(p.getName())
        }

        // initialize components here
    }
}
