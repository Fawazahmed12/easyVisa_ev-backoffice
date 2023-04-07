<%@ page import="com.easyvisa.questionnaire.model.ApplicantType" %>
<div class="selection-table-sticky-body table-sticky-body packages-gsp-sticky-table">
<table class="packages-gsp-table table border-gray-ccc table-bordered small-cells" style='width: 90%; margin-left: 2%;border-collapse: collapse;box-sizing: border-box;border: 1px solid #808285'>
    <thead>
    <tr style='background: #40678c;'>
        <th style='border-right:1px solid white;width: 15%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
            EasyVisa ID
        </th>
        <th style='border-right:1px solid white;width: 15%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
            Benefit Category
        </th>
        <th style='border-right:1px solid white;width: 30%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
            Applicant Type
        </th>
        <th style='border-right:1px solid white;width: 40%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
            Name
        </th>
    </tr>
    </thead>
    <tbody>
    <g:set var="index" value="${1}"/>
    <g:each in="${aPackage.packageApplicantsUi}" var="applicantUi">
        <tr>
            <td style='text-align: center'>${applicantUi.applicant.profile.easyVisaId}</td>
            <td style='text-align: center'><g:if
                    test="${applicantUi.category}">${applicantUi.category.abbreviation}</g:if><g:else><span class="text-secondary">None</span></g:else></td>
            <td style='text-align: left;padding-left: 1%;' class="text-left pl-2"><g:set var="applicantType"
                                                                  value="${ApplicantType.getByUiValue(applicantUi.applicantType)}"/>
            <g:if test="${applicantUi.applicantType == ApplicantType.Derivative_Beneficiary.uiValue}">${applicantType.value}
                ${index}<g:set var="index" value="${index + 1}"/></g:if>
                <g:else>${applicantType.value}</g:else></td>
            <td style='text-align: left;padding-left: 1%;' class="text-left pl-2">${applicantUi.applicant.profile.fullName}</td>
        </tr>
    </g:each>
    </tbody>

</table>
</div>