<%@ page import="com.easyvisa.User; com.easyvisa.Organization; com.easyvisa.Employee; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="represenative" value="${processRequest.representative as com.easyvisa.LegalRepresentative}"/>
    <g:set var="requestedBy" value="${processRequest.requestedBy as com.easyvisa.Profile}"/>
    ${requestedBy.fullName} has invited you to join then in creating a new law firm on EasyVisa!
    <br/>
    if you click on <g:acceptLink
            alertId="${alert.id}"/>, then this new law firm will be created, you will become a partner of this new law firm ,and
    will have full admin rights.
    <br/>
    If you click <g:denyLink
            alertId="${alert.id}"/>, then ${requestedBy.fullName} will receive the below message in their Alerts tab:
    <br/>
    <em>'Unfortunately, ${represenative.profile.fullName} does not want to create a new law firm with you on the EasVisa platform'.</em>
    <br/><br/>
    <p>
        Regards,<br/>
        ${requestedBy.fullName}
    </p>
</div>
