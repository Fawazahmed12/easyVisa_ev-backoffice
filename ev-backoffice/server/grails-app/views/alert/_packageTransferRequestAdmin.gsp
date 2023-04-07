<div>
    The following packages will be transferred to ${processRequest.representative.profile.name} at ${processRequest.representativeOrganization.name}. <br /><br />
    <g:render template="/alert/packageDetails" model="[packages: processRequest.packages]"/>
</div>

<p>
    If this transfer was made in error, click <g:denyLink alertId="${alert.id}"/>
    and the transfer request will be deleted if the recipient has not already accepted the packages.
</p>

<p>
    Regards, <br/>
    ${alert.source}
</p>
