package fhir.client

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import ca.uhn.fhir.model.dstu2.resource.Bundle
import ca.uhn.fhir.model.dstu2.resource.Patient
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum
import ca.uhn.fhir.model.dstu2.valueset.HTTPVerbEnum
import ca.uhn.fhir.model.primitive.DateDt
import ca.uhn.fhir.model.primitive.IdDt
import ca.uhn.fhir.rest.client.IGenericClient
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor
import org.zkoss.zul.Button
import org.zkoss.zul.Datebox
import org.zkoss.zul.Grid
import org.zkoss.zul.Label
import org.zkoss.zul.Textbox


class NewpatientComposer extends zk.grails.Composer {

    Label lbFirstName
    Textbox tbFirstName
    Textbox tbSecondName
    Textbox tbLastName
    Datebox dbDateOfBirth
    Button btnSave
    Button btnFind
    Grid patientList
    def

    FhirContextService fhirContextService

    def onClick_btnFind () {

    }

    def onClick_btnSave () {

        Patient patient = new Patient();
        /*patient.addIdentifier()
                .setSystem("http://acme.org/mrns")
                .setValue("12345");*/
        patient.addName()
                .addFamily(tbLastName.getValue())
                .addGiven(tbFirstName.getValue())
                .addGiven(tbSecondName.getValue());
        patient.setBirthDate(dbDateOfBirth.getValue(), TemporalPrecisionEnum.DAY)
        patient.setId(IdDt.newRandomUuid());

        Bundle bundle = new Bundle();
        bundle.setType(BundleTypeEnum.TRANSACTION);

        bundle.addEntry()
                .setFullUrl(patient.getId().getValue())
                .setResource(patient)
                .getRequest()
                .setUrl("Patient")
                .setMethod(HTTPVerbEnum.POST);

        fhirContextService.retrieve()
        IGenericClient client = fhirContextService.ctx.newRestfulGenericClient("http://localhost:8080/hapi-fhir-jpaserver-example/baseDstu2");
        Bundle resp = new Bundle()

        try {
            resp = client.transaction().withBundle(bundle).execute();

        } catch(ex) {
            println(ex.message)
        };



    }
    def beforeCompose = {window ->

    }
    def afterCompose = { window ->
        //btnSave.setLabel(message["default.button.save.label"])
        // initialize components here
    }
}
