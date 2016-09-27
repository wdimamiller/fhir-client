package fhir.client

import ca.uhn.fhir.context.FhirContext
import com.sun.org.apache.xpath.internal.operations.Or
import grails.transaction.Transactional

@Transactional

class FhirContextService {

    public FhirContext ctx

    def retrieve() {
        if ((ctx == null)||(!(ctx instanceof FhirContext))) {ctx = new FhirContext().forDstu2()}
    }

}
