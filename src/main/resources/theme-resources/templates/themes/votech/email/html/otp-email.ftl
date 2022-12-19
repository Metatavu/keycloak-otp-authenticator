<#ftl output_format="HTML">
<html>
    <body>
        <div style="display: block;">
            <img src="https://uploads-ssl.webflow.com/62ffdb594a84e8354677a15d/62ffe030d7e919b0b69ac01b_votech-logo-p-500.png" width="150" height="38"/>
        </div>
        <div>
            ${kcSanitize(msg("emailCodeBody", code))?no_esc}
        </div>
    </body>
</html>
