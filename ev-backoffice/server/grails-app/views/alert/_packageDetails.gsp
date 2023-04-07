<%@ page import="com.easyvisa.enums.CitizenshipStatus;" %>
<div class="selection-table-sticky-body table-sticky-body packages-gsp-sticky-table">
    <table class="packages-gsp-table table border-gray-ccc table-bordered small-cells"
       style='width: 90%; margin-left: 2%;border-collapse: collapse;box-sizing: border-box;border: 1px solid #808285'>
        <thead>
        <tr style='background: #40678c;'>
            <th style='border-right:1px solid white;width: 10%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
                Status
            </th>
            <th class='width-30 font-weight-bold' style='border-right:1px solid white;width: 25%;text-align: center;color: white;font-weight: 400; top: 0;'>
                Clients
            </th>
            <th style='border-right:1px solid white;width: 10%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
                Legal Status
            </th>
            <th style='border-right:1px solid white;width: 10%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
                Benefit
            </th>
            <th style='border-right:1px solid white;width: 5%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
                Ques.
            </th>
            <th style='border-right:1px solid white;width: 5%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
                Docs
            </th>
            <th style='border-right:1px solid white;width: 15%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
                Last Active
            </th>
            <th style='width: 10%;text-align: center;color: white;font-weight: 400; top: 0;' class="font-weight-bold">
                Owed
            </th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${packages}" var="aPackage">
            <tr>
                <td style='${aPackage.status.name() == 'BLOCKED' ? 'text-align: center;color: red;' : 'text-align: center;color: #006cb7;'}'
                    class="${aPackage.status.name() == 'BLOCKED' ? 'red' : 'blue'}">${aPackage.status.name()?.toLowerCase()?.capitalize()}</td>
                <td class='width-30 text-left' style='text-align: left;padding-left: 1%;'>${aPackage.title}</td>
                <td style='text-align: center'>
                    <g:if test="${aPackage.petitioner?.citizenshipStatus}">
                        <span>
                            <g:if test="${aPackage.petitioner?.citizenshipStatus == CitizenshipStatus.LPR}">
                                <span> ${aPackage.petitioner?.citizenshipStatus} </span>
                            </g:if>
                            <g:else>
                                <span> ${aPackage.petitioner?.citizenshipStatus?.displayName} </span>
                            </g:else>
                        </span>
                    </g:if>
                    <g:else>
                        <g:if test="${aPackage.getDirectBenefit()?.citizenshipStatus == CitizenshipStatus.LPR}">
                            <span>${aPackage.getDirectBenefit()?.citizenshipStatus}</span>
                        </g:if>
                        <g:else>
                            <span> ${aPackage.getDirectBenefit()?.citizenshipStatus?.displayName}</span>
                        </g:else>
                    </g:else>
                </td>
                <td style='text-align: center'>${aPackage.orderedBenefits*.category*.name().join(',')}</td>
                <td style='text-align: center'>
                    <g:if test="${aPackage.questionnaireCompletedPercentage}">
                        <span>${aPackage.questionnaireCompletedPercentage}%</span>
                    </g:if>
                    <g:else>
                        <span>0%</span>
                    </g:else>
                </td>
                <td style='text-align: center'>
                    <g:if test="${aPackage.documentCompletedPercentage}">
                        <span>${aPackage.documentCompletedPercentage}%</span>
                    </g:if>
                    <g:else>
                        <span>0%</span>
                    </g:else>
                </td>
                <td style='text-align: center'>
                    <g:if test="${aPackage.lastActiveOn}">
                        <g:formatDate format="MM/dd/yyyy" date="${aPackage.lastActiveOn}"/>
                    </g:if>
                </td>
                <td style='text-align: center'>
                    <g:if test="${aPackage.owed}">
                        <span>$${aPackage.owed}</span>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
