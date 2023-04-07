<g:set var="organization" value="${processRequest.oldOrganization}"/>
<div>
    ${processRequest.requestedBy.fullName}, an admin at ${organization.name} has transferred ownership of your below listed ${processRequest.packages.size()}
    package(s)/case(s) to another representative on EasyVisa.<br/>
    <g:render template="/alert/packageDetails" model="[packages: processRequest.packages]"/>
</div>

<p>
    If the transfer was done in error, you can <g:denyLink alertId="${alert.id}"/> the request.
</p>

<p>
    Regards, <br/>
    ${alert.source}
</p>