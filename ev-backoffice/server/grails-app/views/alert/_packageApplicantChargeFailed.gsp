<g:set var="applicant" value="${(processRequest ? processRequest.immigrationBenefit.applicant : alert.applicant) as com.easyvisa.Applicant}"/>
<div>
    <p>
        There was a problem processing package applicant fee of ${applicant.name} client.
        Questionnaire and Document Portal won't be accessible for this client until charge is applied.
    </p>

    <p>
        Please verify that the name, card number, expiration date, billing address, billing ZIP code, and
        CVV/CVC code are all correct in My Account > Payment & Fee Schedule tab.
        If the above information is correct, then contact the issuing bank and then pay the balance in My Account > Payment & Fee Schedule tab.
    </p>

    <p>
        You can also try using another credit card.
    </p>

    <p>
        Regards, <br/>
        EasyVisa
    </p>
</div>
