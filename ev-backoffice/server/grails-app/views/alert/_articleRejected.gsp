<div>
    <p>
        Hello ${article.author.profile.name},
    </p>

    <p>
        Unfortunately, the article that you submitted to EasyVisa has not been approved<g:if test = "${article.rejectedMessage}"> for the following reasons:
    </p>

    <p>
        ${article.rejectedMessage}</g:if>
        <g:else>.</g:else>
    </p>

    <p>
        If you make some changes to your article, based on the above suggestion(s), you may re-submit your article.
        Remember, as a way of saying thank you for contributing to the EasyVisa community, if your article is approved your
        mini-profile will appear at the bottom of your article, potentially increasing your impressions on EasyVisa, which can give
        you FREE leads.
    </p>

    <p>
        Finally, as a reward for our members, for every approved article that is posted to EasyVisa, your account will be credited
        with $${credit} towards future billing expenditures!
    </p>

    <p>
        Best regards,<br/>
        The EasyVisa Team
    </p>
</div>