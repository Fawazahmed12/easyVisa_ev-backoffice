<%@ page import="com.easyvisa.Organization;" %>
<div>
    <g:set var="employee" value="${processRequest.employee}"/>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>

    Unfortunately, ${employee.profile.fullName} has declined your invitation to become a member of ${organization.name} on the EasyVisa platform.
    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>