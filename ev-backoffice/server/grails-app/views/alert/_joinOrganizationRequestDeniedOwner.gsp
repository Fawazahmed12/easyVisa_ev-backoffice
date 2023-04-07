<%@ page import="com.easyvisa.Organization; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="employee" value="${processRequest.organizationEmployee.employee}"/>
    <g:set var="organization" value="${processRequest.organizationEmployee.organization as Organization}"/>
    <p>
        Unfortunately, ${employee.profile.fullName} has declined your invitation to become a member of ${organization.name} on the
        EasyVisa platform.
    </p>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>