<%@ page import="com.easyvisa.PackageOptInForImmigrationBenefitRequest; com.easyvisa.enums.PackageStatus; com.easyvisa.PackageService; com.easyvisa.LegalRepresentative; com.easyvisa.AttorneyService; com.easyvisa.Applicant; com.easyvisa.Profile;" %>
<%@ page import="com.easyvisa.PackageOptInForPetitionerRequest;" %>
<div>
    <g:set var="profile" value="${processRequest.requestedBy as Profile}"/>

    <g:if test="${processRequest instanceof PackageOptInForPetitionerRequest}">
        <g:set var="applicant" value="${processRequest.petitioner.applicant as Applicant}"/>
    </g:if>
    <g:elseif test="${processRequest instanceof PackageOptInForImmigrationBenefitRequest}">
        <g:set var="applicant" value="${processRequest.immigrationBenefit.applicant as Applicant}"/>
    </g:elseif>
    <%
        AttorneyService attorneyService = grailsApplication.mainContext.getBean("attorneyService")
        PackageService packageService = grailsApplication.mainContext.getBean("packageService")
        LegalRepresentative legalRepresentative = attorneyService.findAttorneyByUser(profile.user.id)
    %>
    <p>
        ${profile.firstName} ${profile.lastName}, an ${legalRepresentative.representativeType.displayName} on EasyVisa,
        is trying to add you to an Immigration application package.
    </p>

    <p>
        If you would like to be added to this Immigration application package, then click <g:acceptLink
                alertId="${alert.id}"/>.
        After you click Accept other applicants within the package, as well as your ${legalRepresentative.representativeType.displayName},
        will be able to see and edit all of your information in this new application package once it is Opened.
        This does not affect any other packages that you are currently in nor will it affect any prior application packages that you have been a member of.
    </p>

    <p>
        If you do not want to be added to this package, then click <g:denyLink
                alertId="${alert.id}"/> and you will not be included in this new package.
    </p>

    <g:if test="${packageService.isApplicantInSpecificPackageStatus(PackageStatus.BLOCKED, applicant)}">
        <p>
            Additionally, it appears as though you are a member of one or more immigration (application) package(s) that is/are in <span style="color: red; font-weight: bold;">Blocked</span> status.
            This/these packages must all be converted to either Open or Closed status, before your package can be opened
            (which then allows you to begin answering questions in the Questionnaire and uploading documents into the Document portal).
            Please  go to Step 1 in My Account > Representative and refer to the drop down list to see the status of your previous packages and the contact information of the legal representative handling them.
        </p>
    </g:if>

    <g:if test="${packageService.isApplicantInSpecificPackageStatus(PackageStatus.OPEN, applicant)}">
        <p>
            Additionally, it appears as though you are a member of one or more immigration (application) package(s) that is/are in <span style="color: red; font-weight: bold;">Open</span> status.
            If you haven't already, you must disclose this to your ${legalRepresentative.representativeType.displayName} to ensure there are no conflicts that would affect your applications.
            Please  go to Step 1 in My Account > Representative and refer to the drop down list to see the status of your previous packages and the contact information of the legal representative handling them.
        </p>
    </g:if>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>