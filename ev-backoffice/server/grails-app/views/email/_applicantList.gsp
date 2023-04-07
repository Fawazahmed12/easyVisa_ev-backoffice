<%@ page import="com.easyvisa.enums.ImmigrationBenefitCategory; com.easyvisa.questionnaire.model.ApplicantType" %>
<table class="table table-bordered"
       style='width: 95%; margin-left: 2%;border-collapse: collapse;box-sizing: border-box;border: 1px solid #808285'>
    <thead>
    <tr style='background: #40678c;'>
        <th style='padding: 0.3rem;border-right:1px solid white;width: 33%;text-align: center;color: white;font-weight: 400; top: 0;'>EasyVisa ID</th>
        <th style='padding: 0.3rem;border-right:1px solid white;width: 33%;text-align: center;color: white;font-weight: 400; top: 0;'>Applicant Type</th>
        <th style='padding: 0.3rem;width: 33%;text-align: center;color: white;font-weight: 400; top: 0;'>Name</th>
    </tr>
    </thead>
    <tbody>
    <g:if test="${packageObj.petitioner}">
        <tr>
            <td style='text-align: center; padding: 0.3rem;'>${packageObj.petitioner.profile.easyVisaId}</td>
            <td style='text-align: center; padding: 0.3rem;'>Petitioner/Client</td>
            <td style='text-align: center; padding: 0.3rem;'>${packageObj.petitioner.profile.fullName}</td>
        </tr>
    </g:if>
    <g:each status="i" in="${packageObj?.orderedBenefits}" var="benefit">
        <tr>
            <td style='text-align: center; padding: 0.3rem;'>${benefit.applicant.profile.easyVisaId}</td>
            <td style='text-align: center;padding: 0.3rem;'>${benefit.direct ? benefit.category.detectBeneficiaryType().value
                    : ApplicantType.Derivative_Beneficiary.value}</td>
            <td style='text-align: center;padding: 0.3rem;'>${benefit.applicant.profile.fullName}</td>
        </tr>
    </g:each>
    </tbody>
</table>
