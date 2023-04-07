<g:if test="${packageObj.petitioner}">
    Petitioner: ${packageObj.petitioner.profile.fullName} <br/>
</g:if>
${packageObj.directBenefit.category.detectBeneficiaryType().value}: ${packageObj.principalBeneficiary?.profile?.fullName} <br/>
<g:each status="i" in="${packageObj?.orderedBenefits}" var="benefit">
    <g:if test="${!benefit.direct}">
        Derivative Beneficiary ${i}: ${benefit.applicant.profile?.fullName} <br/>
    </g:if>
</g:each>
