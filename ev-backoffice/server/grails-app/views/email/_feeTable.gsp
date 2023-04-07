<%@ page import="com.easyvisa.utils.NumberUtils" %>
<table class='table' style='width: 90%; border-collapse: collapse;box-sizing: border-box;'>
    <thead>
        <tr style='border-bottom:1px solid black;'>
            <th style='width: 70%; text-align: center'>Description of Services</th>
            <th style='width: 8%; text-align: center'>Each</th>
            <th style='width: 7%; text-align: center'>Qty</th>
            <th class="text-right" style='width: 15%; text-align: center'>Subtotal</th>
        </tr>
    </thead>
    <tbody>
        <g:each status="i" in="${charges}" var="charge">
            <tr>
                <td style='text-align: left'>${charge.description}</td>
                <td style='text-align: right'>$${NumberUtils.formatMoneyNumber(charge.each)}</td>
                <td style='text-align: right'>${charge.qty}</td>
                <td class="text-right" style='text-align: right'>$${NumberUtils.formatMoneyNumber(charge.subTotal)}</td>
            </tr>
        </g:each>
        <tr style='border-top:5px solid black;'>
            <td class="text-right" colspan="4" style='text-align: right; width: 100%; font-weight: bold;'>Total $${NumberUtils.formatMoneyNumber(total)}</td>
        </tr>
    </tbody>
</table>
