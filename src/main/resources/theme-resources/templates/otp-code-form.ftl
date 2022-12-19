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
                        <div class="${properties.kcFormGroupClass!}">
                            <label for="otpCode"
                                   class="${properties.kcLabelClass!}">${msg("otpCodeInputLabel")}</label>

                                <input tabindex="1" id="otpCode"
                                       aria-invalid="<#if messagesPerField.existsError('otpCode')>true</#if>"
                                       class="${properties.kcInputClass!}" name="otpCode"
                                       type="number" autofocus autocomplete="off"/>

                            <#if messagesPerField.existsError('otpCode')>
                                <span id="input-error-otpCode" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('otpCode'))?no_esc}
                                </span>
                            </#if>
                        </div>

                        <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                            <input tabindex="4"
                                   class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="otpCode" id="kc-login" type="submit" value="${msg("doSubmit")}"/>
                            <input tabindex="4"
                                   class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="resendCode" id="kc-login" type="submit" value="${msg("resendCode")}"/>
                            <input tabindex="4"
                                   class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="cancel" id="kc-login" type="submit" value="${msg("doCancel")}"/>
                        </div>
                    </form>
                </#if>
            </div>
        </div>
    </#if>


</@layout.registrationLayout>