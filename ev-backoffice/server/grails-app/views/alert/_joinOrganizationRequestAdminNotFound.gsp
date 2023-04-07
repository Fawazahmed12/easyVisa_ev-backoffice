<%@ page import="com.easyvisa.AttorneyService; grails.plugin.springsecurity.SpringSecurityService; com.easyvisa.Employee; com.easyvisa.Organization" %>
<div>
    <g:set var="organization" value="${processRequest.organization as Organization}"/>
    <%
        SpringSecurityService springSecurityService = grailsApplication.mainContext.getBean("springSecurityService");
        AttorneyService attorneyService = grailsApplication.mainContext.getBean("attorneyService");
        Employee admin = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)
    %>
    <p>
        We see you made a request to ${admin.profile.fullName} at ${organization.name}.
        Unfortunately, they are no longer an admin there, so you must resubmit your request to another admin at that organization.
        Alternatively, an admin there can invite you to join.
    </p>

    <p>
        Regards,<br/>
        EasyVisa
    </p>
</div>