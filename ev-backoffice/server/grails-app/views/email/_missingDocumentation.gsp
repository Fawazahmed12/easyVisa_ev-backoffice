<ul>
    <g:each in="${missingDocuments}" var="doc">
        <li>${doc.key}
            <ol>
                <g:each in="${doc.value}" var="person">
                    <li>${person.key}
                        <ul>
                            <g:each in="${person.value}" var="item">
                                <li>${item}</li>
                            </g:each>
                        </ul>
                    </li>
                </g:each>
            </ol>
        </li>
    </g:each>
</ul>
