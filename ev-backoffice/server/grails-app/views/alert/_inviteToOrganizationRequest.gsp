<%@ page import="com.easyvisa.Organization" %>
<div>
    <g:set var="employee" value="${processRequest.employee}"/>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>
    ${organization.name} has invited you to join their ${organization.organizationType.displayName} on the EasyVisa platform.<br/>

    Click on <g:acceptLink
            alertId="${alert.id}"/> if you want to become a member of this ${organization.organizationType.displayName} on the EasyVisa platform.<br/>
    If you click <g:denyLink
            alertId="${alert.id}"/>, then the admin of ${organization.name} will receive the below message in their Alerts tab:<br/>
    <em>'Unfortunately, ${employee.profile.fullName} has declined your invitation to become a member of ${organization.name} on the EasyVisa platform'.</em>
    <br/><br/>
    <p>
        Regards,<br/>
        ${employee.profile.fullName}
    </p>
    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>
