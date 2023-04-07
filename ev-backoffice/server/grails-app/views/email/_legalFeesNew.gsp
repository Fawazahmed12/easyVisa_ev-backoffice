<%@ page import='com.easyvisa.utils.StringUtils; com.easyvisa.questionnaire.model.ApplicantType' %>
<table style='width: 90%; margin-left: 3%;border-collapse: collapse;'>
    <tr>
        <th style='text-align: center; border-bottom:1px solid lightgrey ;width: 20%'>EasyVisa ID</th>
        <th style='border-bottom:1px solid lightgrey;width: 20%'>Applicant Type</th>
        <th style='text-align: center;border-bottom:1px solid lightgrey;width: 35%'>Name</th>
        <th style='text-align: right;border-bottom:1px solid lightgrey;width: 25%; padding-right: 20px;'>Fees</th>
    </tr>
    <g:if test="${packageObj.petitioner}">
        <tr>
            <td style='text-align: center;width: 20%'>${packageObj.petitioner.profile.easyVisaId}</td>
            <td style='width: 20%'>${ApplicantType.Petitioner.value}:</td>
            <td style='width: 35%; font-weight: bold;'>${packageObj.petitioner.profile.fullName}</td>
            <td style='text-align: right;width: 25%; padding-right: 15px;'>
                None
            </td>
        </tr>
    </g:if>
    <g:each status="i" in='${packageObj?.orderedBenefits}' var='benefit'>
        <tr>
            <td style='text-align: center;width: 20%'>${benefit.applicant.profile.easyVisaId}</td>
            <td style='width: 20%'>${benefit.direct ? benefit.category.detectBeneficiaryType().value : ApplicantType.Derivative_Beneficiary.value + " " + i}:</td>
            <td style='width: 35%;  font-weight: bold;'>${benefit.applicant.profile.fullName}</td>
            <td style='text-align: right;width: 25%; padding-right: 15px;'>
                ${StringUtils.formatAsMoney(benefit.fee, true)}
            </td>
        </tr>
    </g:each>
    <tr>
        <td style='border-top:2px solid black;text-align: right'></td>
        <td style='border-top:2px solid black;text-align: right'></td>
        <td style='border-top:2px solid black;text-align: right'></td>
        <td style='border-top:2px solid black; padding-right: 15px;'>
            <div>
                <span style="display: inline-block; float:left; width: 50%; text-align: left;">Credit: </span>
                <span style="width: 50%;text-align: right; display: inline-block; float:left;">${StringUtils.formatAsMoney(0)}</span>
            </div>
            <div>
                <span style="display: inline-block; float:left; width: 50%; text-align: left;">Subtotal: </span>
                <span style="width: 50%;text-align: right;display: inline-block; float:left;">${StringUtils.formatAsMoney(packageObj?.orderedBenefits?.sum { it.fee })}</span>
            </div>
            <div>
                <span style="display: inline-block; float:left; width: 50%; text-align: left;">Estimated Tax: </span>
                <span style="width: 50%;text-align: right;display: inline-block; float:left;">${StringUtils.formatAsMoney(0)}</span>
            </div>
            <div style="font-weight:bold;">
                <span style="display: inline-block; float:left; width: 50%; text-align: left;">Grand Total: </span>
                <span style="width: 50%;text-align: right;display: inline-block; float:left;">${StringUtils.formatAsMoney(packageObj?.orderedBenefits?.sum { it.fee })}</span>
            </div>
        </td>
    </tr>
</table>
