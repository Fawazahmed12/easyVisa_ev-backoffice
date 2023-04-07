<div>
    ${alert.source} has requested to transfer ownership of below listed ${processRequest.packages.size()} package(s)/case(s) to you. <br/>
    <g:render template="/alert/packageDetails" model="[packages: processRequest.packages]"/>
</div>

<p>
    If you click Accept below, then all of the above cases will then appear in your 'Clients' tab within your Task Queue.
    Additionally, all of the requesting legal representative's clients will be notified that you have agreed to become
    their new Legal Representative on EasyVisa.
</p>

<p>
    Click on <g:acceptLink
            alertId="${alert.id}"/> to transfer all of these cases to your account.
</p>

<p>
    If you click <g:denyLink
            alertId="${alert.id}"/> then those cases wont be transferred to you and the requesting legal representative
    will be notified that you have denied the request.
</p>

<p>
    Regards, <br/>
    ${alert.source}
</p>