<%@ page import="com.easyvisa.PackageOptInForImmigrationBenefitRequest; com.easyvisa.Applicant; com.easyvisa.Package; com.easyvisa.PackageOptInForPetitionerRequest;" %>
<div>
    <g:set var="aPackage" value="${processRequest.aPackage as Package}"/>
    <g:if test="${processRequest instanceof PackageOptInForPetitionerRequest}">
        <g:set var="applicant" value="${processRequest.petitioner.applicant as Applicant}"/>
    </g:if>
    <g:elseif test="${processRequest instanceof PackageOptInForImmigrationBenefitRequest}">
        <g:set var="applicant" value="${processRequest.immigrationBenefit.applicant as Applicant}"/>
    </g:elseif>

    <p>
        Unfortunately, ${applicant.name} has DENIED your request to add him/her to the immigration application package.
    </p>

    <p>
        You may email them at ${applicant.profile.email} to inquire why.
    </p>

    <g:if test="${aPackage.petitioner}">
        <p>
            The petitioner for this package:<br/>
            ${aPackage.petitioner.name}<br/>
            ${aPackage.petitioner.profile.email}
        </p>
    </g:if>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>