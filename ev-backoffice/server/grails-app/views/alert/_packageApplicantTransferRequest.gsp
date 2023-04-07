<div>
    ${alert.source} has invited you become their new legal representative for the below package of applicants: <br/>
    <g:render template="/alert/packageApplicantDetails" model="[aPackage: processRequest.aPackage]"/>
</div>

<p>
    Click on <g:acceptLink
            alertId="${alert.id}"/> to receive this client’s immigration package and become their legal representative.
    The client’s immigration package will then appear in your Clients tab within your Task Queue.
    The client will be notified that you have agreed to become their new legal representative.
</p>

<p>
    If you click <g:denyLink
            alertId="${alert.id}"/>, then this package will not be transferred to you and the client will be notified.
</p>

<p>
    Regards, <br/>
    EasyVisa
</p>
