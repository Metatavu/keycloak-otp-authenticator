<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('phoneNumber'); section>
    <#if section = "header">
        ${msg("otpCodeFormTitle")}
    <#elseif section = "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <#if realm.password>
                    <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}"
                          method="post">
                        <div class="${properties.customFormGroupClass!}">
                        <#list 1..codeLength as item>
                            <input class="${properties.customInputClass}"
                                <#if item == 1>
                                    autofocus
                                </#if>
                                tabindex="${item}"
                                maxlength=1
                                minlength=1
                                name="code-${item}"/>
                        </#list>
                            <#if messagesPerField.existsError('otpCode')>
                                <span id="input-error-otpCode" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('otpCode'))?no_esc}
                                </span>
                            </#if>
                        </div>

                        <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                            <input tabindex="${codeLength + 1}"
                                   class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="otpCode" id="submit-otp-code" type="submit" value="${msg("doSubmit")}"/>
                            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="resendCode" id="resend-otp-code" type="submit" value="${msg("resendCode")}"/>
                            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="cancel" id="cancel" type="submit" value="${msg("doCancel")}"/>
                        </div>
                    </form>
                </#if>
            </div>
        </div>
    </#if>


</@layout.registrationLayout>