Here are the legal fees we discussed for the revised applicant(s):

<table style='width: 90%; margin-left: 2%;border-collapse: collapse;'>
    <tr>
        <th style='border-bottom:2px solid black;width: 15%'>EasyVisa ID</th>
        <th style='border-bottom:2px solid black;width: 15%'>Applicant Type</th>
        <th style='border-bottom:2px solid black;width: 45%'>Name</th>
        <th style='border-bottom:2px solid black;width: 20%'>Fees</th>
    </tr>
    <g:each in='${packageObj?.orderedBenefits}' var='benefit'>
        <tr>
            <td style='text-align: center'>${benefit.applicant.profile.easyVisaId}</td>
            <td style='text-align: center'>${benefit.direct ? 'Beneficiary' : 'Derivative Beneficiary'}</td>
            <td style='text-align: center'>${benefit.applicant.profile.fullName}</td>
            <td style='text-align: center'>
                ${StringUtils.formatAsMoney(benefit.fee, true)}
            </td>
        </tr>
    </g:each>
    <tr>
        <td style='border-top:2px solid black;text-align: right'></td>
        <td style='border-top:2px solid black;text-align: right'></td>
        <td style='border-top:2px solid black;text-align: right'></td>
        <td style='border-top:2px solid black;text-align: center; font-weight:bold'>

            Total New Fees ${StringUtils.formatAsMoney(packageObj?.orderedBenefits?.sum { it.fee })}
        </td>
    </tr>
</table>

This bill ONLY reflects new charges. If there is still an outstanding balance for previously owed fees, please make arrangements to paythat balance as soon as possible.
