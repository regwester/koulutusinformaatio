<#include "macros.ftl">
<!DOCTYPE html>
<html>
<#-- <#include "resources.ftl"> -->
<head>
<#include "head.ftl">
</head>
<body>
<#include "top.ftl">
<#assign providersBaseUrl = "${baseUrl}${lang}/hakemisto/oppilaitokset"/>
<#assign educationBaseUrl = "${baseUrl}${lang}/hakemisto/koulutukset"/>
<div id="page">
    <div class="grid16-16">
        <div class="buttongroup margin-bottom-4">
        <#list alphabets as alphabet>
            <#if alphabet == letter>
                <a href="javascript:void(0);" class="button active">${alphabet}</a>
            <#elseif validCharacters?seq_contains(alphabet)>
                <a href="${providersBaseUrl}/${alphabet}" class="button">${alphabet}</a>
            <#else>
                <a href="javascript:void(0);" class="button disabled">${alphabet}</a>
            </#if>
        </#list>
        </div>
        <div class="clear"></div>
        <p>
        <#list providerTypes as providerType>

            <#if providerType.value == "99">
                <#assign providerName><@msg "provider.type.other"/></#assign>
            <#else>
                    <#assign providerName = providerType.name>
            </#if>

            <#if providerType.value == selectedProviderType>
                ${providerName}
            <#else>
                <a href="${providersBaseUrl}/${letter}/${providerType.value}">${providerName}</a>
            </#if>
            <#if providerType_has_next> | </#if>

        </#list>
        </p>

        <div class="clear"></div>
        <p>
        <#list providers as provider>
            <a href="${educationBaseUrl}/${provider.id}">${provider.name}</a>
            <br>
        </#list>
        </p>
    </div>
</div>
<div class="clear"></div>
<#include "footer.ftl">
<#include "scripts.ftl">
</body>
</html>