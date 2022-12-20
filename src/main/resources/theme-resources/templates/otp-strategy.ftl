<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('phoneNumber'); section>
    <#if section = "header">
        ${msg("otpStrategyWelcome")}
        <p id="otp-strategy-instruction">${msg("otpStrategyHelper")}</p>
    <#elseif section = "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <#if realm.password>
                    <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}"
                          method="post">
                        <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                   name="email" id="kc-login" type="submit" value="${msg("otpStrategyEmailSelection")}"/>
                           <hr id="otp-form-hr"/>
                           <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                  name="sms" id="kc-login" type="submit" value="${msg("otpStrategySmsSelection")}"/>
                        </div>
                    </form>
                </#if>
            </div>
        </div>
    </#if>


</@layout.registrationLayout>