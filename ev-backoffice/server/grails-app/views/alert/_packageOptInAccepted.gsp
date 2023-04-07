<%@ page import="com.easyvisa.PackageOptInForImmigrationBenefitRequest; com.easyvisa.Applicant; com.easyvisa.PackageOptInForPetitionerRequest;" %>
<div>
    <g:if test="${processRequest instanceof PackageOptInForPetitionerRequest}">
        <g:set var="applicant" value="${processRequest.petitioner.applicant as Applicant}"/>
    </g:if>
    <g:elseif test="${processRequest instanceof PackageOptInForImmigrationBenefitRequest}">
        <g:set var="applicant" value="${processRequest.immigrationBenefit.applicant as Applicant}"/>
    </g:elseif>

    <p>
        Congratulations, ${applicant.name} has accepted your request to add him/her to the package.
    </p>

    <p>
        Once all of the applicants within the this package (who were previously EasyVisa applicants) have clicked the Accept buttons within their emails, then you can Open the package which will allow both you and your clients to begin working on the EasyVisa platform.
    </p>

    <p>
        However, if any applicant in this package has another package on the EasyVisa platform that is in Blocked status, they must have that package converted to either Open or Closed status before this package can be Opened.

    <p>
        Similarly, if any applicant within this package has an package on the EasyVisa platform that is in Open status, you must discuss it with them to ensure there will be no issues with this package.
    </p>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>