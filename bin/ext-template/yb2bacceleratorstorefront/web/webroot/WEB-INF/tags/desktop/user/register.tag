<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="register.new.customer"/></h2>
	</div>
	
	<div class="item_container">
		<p><spring:theme code="register.description"/></p>
		<p class="required"><spring:theme code="form.required"/></p>
		
		<form:form method="post" commandName="registerForm" action="${action}">
			<c:if test="${not empty message}">
				<span class="errors">
					<spring:theme code="${message}"/>
				</span>
			</c:if>
			<dl>
				<c:if test="${not empty accErrorMsgs}">
					<span class="form_field_error">
				</c:if>

				<formUtil:formCheckbox idKey="types" labelKey="register.supplier" path="type" inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false"/>
				<formUtil:formInputBox idKey="name" labelKey="register.name" path="name" inputCSS="text" mandatory="true"/>
				<formUtil:formInputBox idKey="cName" labelKey="register.companyName" path="cName" inputCSS="text" mandatory="true"/>
				<formUtil:formInputBox idKey="email" labelKey="register.email" path="email" inputCSS="text" mandatory="true"/>
				<formUtil:formInputBox idKey="phone" labelKey="register.phone" path="phone" inputCSS="text" mandatory="true"/>
				<formUtil:formPasswordBox idKey="password" labelKey="register.pwd" path="pwd" inputCSS="text password strength" mandatory="true"/>
				<formUtil:formPasswordBox idKey="register.checkPwd" labelKey="register.checkPwd" path="checkPwd" inputCSS="text password" mandatory="true"/>

				<c:if test="${not empty accErrorMsgs}">
					</span>
				</c:if>
			</dl>
			<span style="display: block; clear: both;">
			<ycommerce:testId code="register_Register_button">
				<button type="submit" class="positive"><spring:theme code="${actionNameKey}"/></button>
			</ycommerce:testId>
			</span>
		</form:form>
	</div>	
</div>



