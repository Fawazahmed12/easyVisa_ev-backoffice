<%@ page import="com.easyvisa.User; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="represenative" value="${processRequest.representative as com.easyvisa.LegalRepresentative}"/>
    <g:set var="requestedBy" value="${processRequest.requestedBy as com.easyvisa.Profile}"/>
    ${requestedBy.fullName} has withdrawn their invitation to create a new legal practice with you.<br/><br/>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>