<%@ page import="com.easyvisa.enums.OrganizationType; com.easyvisa.Organization; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>
    <g:set var="organizationType" value="${processRequest.organization.organizationType as OrganizationType}"/>
    <g:set var="employee" value="${processRequest.employee}"/>
    <p>${processRequest.requestedBy.fullName} has requested to join your ${organization.organizationType.displayName} on EasyVisa platform.</p>

    <p>Click on <g:acceptLink
            alertId="${alert.id}"/> to allow them to become a member of this ${organizationType.displayName}. By clicking 'ACCEPT' this
    ${employee.instanceOf(LegalRepresentative) ? (LegalRepresentative.get(employee.id).representativeType.displayName) : 'user'}
    will be associated with your ${organizationType.displayName} and will be allowed to access packages as a member of your ${organizationType.displayName}.</p>

    <p>If you click <g:denyLink
            alertId="${alert.id}"/>, the requester will receive the below message in their Alerts tab:</p>

    <p><em>'Unfortunately, your request to join the ${organization.name} ${organizationType.displayName} has been declined.'</em></p>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>
