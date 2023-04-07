<%@ page import="com.easyvisa.Organization;" %>
<div>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>
    <p>
        The invitation to join ${organization.name} has been withdrawn.
    </p>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>