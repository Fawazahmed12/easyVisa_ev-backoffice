<div>
    <p>
        The following packages were in previous transfer requests that were still pending acceptance.
        Those transfer requests were overridden to ${processRequest.representative.profile.name} (${processRequest.representativeOrganization.name}).
    </p>

    <div class="selection-table-sticky-body table-sticky-body packages-gsp-sticky-table">

        <table class="packages-gsp-table table border-gray-ccc table-bordered small-cells">
            <thead>
            <tr style='background: #40678c;'>
                <th style='border-right:1px solid white;width: 50%;text-align: center;color: white;font-weight: 400; top: 0;'
                    class="font-weight-bold">
                    Package
                </th>
                <th class='font-weight-bold'
                    style='border-right:1px solid white;width: 50%;text-align: center;color: white;font-weight: 400; top: 0;'>
                    Previous Transferee
                </th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${info}" var="details">
                <tr>
                    <td>${details.key}</td>
                    <td>${details.value}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <p>
        Regards, <br/>
        EasyVisa
    </p>
</div>
