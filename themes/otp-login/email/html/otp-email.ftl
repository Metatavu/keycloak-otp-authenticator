<#ftl output_format="HTML">
<html>
    <body>
        <div>
            ${kcSanitize(msg("emailCodeBody", code))?no_esc}
        </div>
    </body>
</html>
