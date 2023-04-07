<%@ page import="com.easyvisa.User; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="represenative" value="${processRequest.representative as com.easyvisa.LegalRepresentative}"/>
    <g:set var="requestedBy" value="${processRequest.requestedBy as com.easyvisa.Profile}"/>

    Congratulations, ${represenative.profile.fullName} has accepted your request to join them in creating a new legal
    practice on EasyVisa!<br/><br/>

    <p>
        The two founding attorneys of a legal practice on EasyVisa are considered to be 'partners'
        and are therefore also have admin privileges on the EasyVisa platform.
    </p>

    <p>
        As the admin you can enter/edit all the data about your law firm in the Law Firm profile panel
        (located below your personal Attorney profile panel in My Account > Profile).
        After the legal practice is created, then any successive attorneys who join your legal practice will initially
        be non-admin attorneys and will show up in your My Account > Permissions tab. Additionally,
        admins can de-activate members of the firm, change their status with the firm (i.e. promote non-admin attorneys
        to partner attorneys), edit users' access privileges to employee files and client files, and re-assign
        admin privileges from yourself to another partner (or employee) at your law firm.
    </p>

    <p>
        Additionally, when attorneys of the same legal practice are linked to the legal practice, then the EasyVisa
        platform becomes a very powerful tool for the practice, because each member can see and work on all files of
        the legal practice, and all metrics for the practice will also be combined together in one screen.
    </p>
    <br/><br/>
    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>
