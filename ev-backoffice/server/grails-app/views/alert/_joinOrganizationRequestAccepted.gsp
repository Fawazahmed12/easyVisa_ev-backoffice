<%@ page import="com.easyvisa.enums.OrganizationType; com.easyvisa.Organization; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="employee" value="${processRequest.employee}"/>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>
    <g:set var="organizationType" value="${processRequest.organization.organizationType as OrganizationType}"/>
    <p>
        Congratulations!
    </p>

    <p>
        <g:if test="${employee.instanceOf(LegalRepresentative)}">
            ${alert.source} has accepted your request to join ${organization.name} on the EasyVisa platform.
            Your name will now appear in your Permissions tab as a member of ${organization.name} ${organizationType.displayName}.
            Your ${organizationType.displayName}’s name will also appear in your profile in search results.
            Finally, you will now be able to create packages as a member of this ${organizationType.displayName}.
        </g:if>
        <g:else>
            ${alert.source} has accepted your request to join ${organization.name} on the EasyVisa platform.
            Your name will now appear in your Permissions tab as a member of ${organization.name} ${organizationType.displayName}.
            Initially, your permissions status will be assigned to be a ‘Trainee’.
            Any admin of your new organization can upgrade your permissions level to Employee or Manager within My Account > Permissions.
        </g:else>
    </p>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>