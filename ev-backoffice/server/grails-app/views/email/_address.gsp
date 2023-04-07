<g:if test="${address}">
    <span>${address.line1}</span> <br/>
    <g:if test="${address.line2}">
        <span>${address.line2}</span> <br/>
    </g:if>
    <span>${address.city}, ${address.state?.code ?: address.province} &nbsp;${address.zipCode ?: address.postalCode}</span> <br/>
    <span>${address.country?.displayName}</span>
</g:if>