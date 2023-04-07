<g:if test="${legalRepresentative.officeAddress}">
    <g:render template="/email/address" model="[address: legalRepresentative.officeAddress]"/>
</g:if>