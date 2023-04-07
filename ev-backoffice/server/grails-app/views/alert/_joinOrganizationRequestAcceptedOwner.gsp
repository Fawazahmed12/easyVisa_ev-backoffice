<%@ page import="com.easyvisa.Organization; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="employee" value="${processRequest.organizationEmployee.employee}"/>
    <g:set var="organization" value="${processRequest.organizationEmployee.organization as Organization}"/>
    <g:if test="${employee.instanceOf(LegalRepresentative)}">
        ${employee.profile.fullName} has accepted your invitation to join ${organization.name} on the EasyVisa platform. Their name will now
        appear in your Permissions tab as a member of your ${organization.organizationType.displayName} and they can also begin creating packages as a
        member of your ${organization.organizationType.displayName}.
    </g:if>
    <g:else>
        ${employee.profile.fullName} has accepted your invitation to join ${organization.name} on the EasyVisa platform. Their name will now
appear in your Permissions tab as a member of your ${organization.organizationType.displayName}.
    </g:else>
    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>