<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('phoneNumber'); section>
    <#if section = "header">
        ${msg("phoneNumberLoginFormTitle")}
    <#elseif section = "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <#if realm.password>
                    <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}"
                          method="post">
                        <div class="${properties.kcFormGroupClass!}">
                            <label for="phoneNumber"
                                   class="${properties.kcLabelClass!}">${msg("phoneNumber")}</label>

                                <input tabindex="1" id="phoneNumber"
                                       aria-invalid="<#if messagesPerField.existsError('phoneNumber')>true</#if>"
                                       class="${properties.kcInputClass!}" name="phoneNumber"
                                       type="text" autofocus autocomplete="off"/>

                            <#if messagesPerField.existsError('phoneNumber')>
                                <span id="input-error-phoneNumber" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('phoneNumber'))?no_esc}
                                </span>
                            </#if>
                        </div>

                        <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                            <input tabindex="4"
                                   class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="login" id="kc-login" type="submit" value="${msg("doSendCode")}"/>
                        </div>
                    </form>
                </#if>
            </div>
        </div>
    </#if>


</@layout.registrationLayout>