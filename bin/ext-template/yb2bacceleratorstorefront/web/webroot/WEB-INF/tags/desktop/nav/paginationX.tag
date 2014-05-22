<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="msgKey" required="false" %>


<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/desktop/nav/pagination" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}"/>


<div class="prod_refine">
	<spring:theme code="text.account.orderHistory.page.filterUnpaidResults" /> 
	<input id="filterUnpaid" type="checkbox" onclick="filterUnpaid()"/>
	<button type="button" id="payNowBtn" class="right" onclick="payNow()">
		<spring:theme code="${text.account.orderHistory.page.paynow}" text="Pay Now"/>
	</button>

</div>