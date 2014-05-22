<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="span-10 last forgotten-pwd">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2>产品询价</h2>
		</div>

		<div class="item_container">
			<p>请描述您的需求</p>
			<p class="required"><spring:theme code="form.required"/></p>
			<form:form method="post" commandName="quoteProductForm">
                <div class="form_field-elements">
					<div class="form_field-input">
						<form:textarea path="description" rows="10" cols="60" />
					</div>
				</div>
				<span style="display: block; clear: both;">
					<button class="form"><!-- <spring:theme code="forgottenPwd.submit"/>-->发送</button>
				</span>
			</form:form>
		</div>

</div>
