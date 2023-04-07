<%@ page import="com.easyvisa.Organization; com.easyvisa.LegalRepresentative" %>
<div>
    <g:set var="employee" value="${processRequest.employee}"/>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>
    <g:if test="${employee.instanceOf(LegalRepresentative)}">
        ${employee.profile.fullName} has accepted your invitation to join ${organization.name} on the EasyVisa platform.
        Their name will now appear in your Permissions tab as a member of your ${organization.organizationType.displayName}
        and they can also begin creating packages as a member of your ${organization.organizationType.displayName}.
    </g:if>
    <g:else>
        ${employee.profile.fullName} has accepted your invitation to join ${organization.name} on the EasyVisa platform.
        Their name will now appear in your Permissions tab as a member of your ${organization.organizationType.displayName}.
        Initially, all new employees have a Permissions status of ‘Trainee’, which does not allow them to create/edit packages.
        You can easily allow this new employee to create/edit packages by changing their Position to Employee or Manager by clicking on
        the employee’s name (after they have accepted your invitation) in the My Account > Permissions table of your ${organization.organizationType.displayName} members.
    </g:else>
    <br/><br/>
    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>
