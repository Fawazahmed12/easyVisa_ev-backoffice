<div>
    ${oldRep.profile.fullName} has transferred your immigration case, with the below listed applicants to ${newRep.profile.fullName}
</div>
<table class="packages-gsp-table" style='width: 90%; margin-left: 2%;border-collapse: collapse;box-sizing: border-box;border: 1px solid #808285'>
    <tr style='background: #40678c;'>
        <th style="border-right:1px solid white;width: 20%;text-align: center;color: white;font-weight: 400; top: 0;">EasyVisa ID</th>
        <th style="border-right:1px solid white;width: 20%;text-align: center;color: white;font-weight: 400; top: 0;">Applicant Type</th>
        <th style="border-right:1px solid white;width: 60%;text-align: center;color: white;font-weight: 400; top: 0;">Name</th>
    </tr>
    <g:each in="${packageObj?.orderedBenefits}" var="benefit">
        <tr>
            <td style="border-bottom:1px solid black; border-right: 1px solid black; text-align: center;">${benefit.applicant.profile.easyVisaId}</td>
            <td style="border-bottom:1px solid black; border-right: 1px solid black; text-align: center;">${benefit.direct ? 'Beneficiary' : 'Derivative Beneficiary'}</td>
            <td style="border-bottom:1px solid black; border-right: 1px solid black; text-align: center;">${benefit.applicant.profile.fullName}</td>
        </tr>
    </g:each>
</table>

<p>
    If you have any questions, you can contact either of these legal representatives at the below contact numbers:
</p>

<p>
    Former ${oldRep.representativeType.displayName} : <br/>
    Work: ${oldRep.officePhone} <br/>
    Mobile: ${oldRep.mobilePhone} <br/>
    email: ${oldRep.profile.email} <br/>
</p>

<p>
    New ${newRep.representativeType.displayName} : <br/>
    Work: ${newRep.officePhone} <br/>
    Mobile: ${newRep.mobilePhone} <br/>
    email: ${newRep.profile.email} <br/>
</p>

<div>
    Regards, <br/>
    EasyVisa
</div>
