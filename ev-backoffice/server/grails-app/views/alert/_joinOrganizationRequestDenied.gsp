<%@ page import="com.easyvisa.enums.OrganizationType; com.easyvisa.Organization;" %>
<div>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>
    <g:set var="organizationType" value="${processRequest.organization.organizationType as OrganizationType}"/>
    <p>
        Unfortunately, the admin of ${organization.name} has declined your request to join their ${organizationType.displayName} on the EasyVisa platform.
    </p>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>