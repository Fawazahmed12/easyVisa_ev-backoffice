<%--
  Created by IntelliJ IDEA.
  User: sa1nt
  Date: 1/2/19
  Time: 5:38 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
</head>

<body>
<form id="testForm" enctype="multipart/form-data">
    <input type="file" name="testfile" id="testFile"/> <br/> <br/>
    AccessToken <textarea id="tokenText" rows="15" cols="150"></textarea> <br/> <br/>

    <button type="submit" value="Submit">Submit</button>

    <image src="http://localhost:8080/api/public/users/33/profile-picture">
</form>
<script>

    $(document).ready(function () {
        $('#testForm').on('submit', function () {
            sendReq();
            return false;
        })
    });

    function sendReq() {
        var formData = new FormData();
        console.log($('#testFile')[0].files[0])
        formData.append('profilePhoto', $('#testFile')[0].files[0]);

        $.ajax({
            url: '/api/users/33/profile-picture',
            data: formData,
            type: 'POST',
            enctype: 'multipart/form-data',
            headers: {'Authorization': 'Bearer ' + $('#tokenText').val()},
            contentType: false,
            processData: false,
        }).done(function (d) {
            alert(d.message)
        }).fail(function (d, x1) {
            var message = d.responseJSON.errors[0].message
            alert(message)
        });
    }
</script>
</body>
</html>