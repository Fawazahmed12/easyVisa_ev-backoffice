<%@ page import="com.easyvisa.User; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="represenative" value="${processRequest.representative as com.easyvisa.LegalRepresentative}"/>
    <g:set var="requestedBy" value="${processRequest.requestedBy as com.easyvisa.Profile}"/>
    Unfortunately, ${represenative.profile.fullName} has denied your invitation to create a new law firm with you on the EasyVisa platform.<br/><br/>
    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>