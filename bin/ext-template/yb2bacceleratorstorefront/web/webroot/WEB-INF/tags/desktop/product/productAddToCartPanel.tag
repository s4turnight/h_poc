<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<c:choose>
	<c:when test="${empty product.variantOptions}">
		<c:set var="allowAddToCart" value="${true}"/>
	</c:when>
	<c:otherwise>
		<c:set var="allowAddToCart" value="${false}"/>
	</c:otherwise>
</c:choose>

<div class="prod_add_to_cart">

	<form id="addToCartForm" class="add_to_cart_form" action="<c:url value="/cart/add"/>" method="post">
		<div class="span-8">
			<c:if test="${product.purchasable}">
				<label for="qty"><spring:theme code="basket.page.quantity" /></label>
				<input type="text" maxlength="3"  size="1" id="qty" name="qty" class="qty" value="1">
			</c:if>
			<input type="hidden" name="productCodePost" value="${product.code}"/>

			<span class="prod_results">
				<product:productFutureAvailability product="${product}" futureStockEnabled="${futureStockEnabled}" />
			</span>
		</div>

		<div class="span-8 viewDetailButton">
		
			<c:set var="buttonType">button</c:set>
			<c:if test="${allowAddToCart and product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
				<c:set var="buttonType">submit</c:set>
			</c:if>
			
			<button id="quoteBtn" type="button" data-pcode="${product.code}" data-url="<c:url value='/quote/product/request/${product.code}'/>">
				<spring:theme code="checkout.summary.negotiateQuote"/>
			</button>

			<spring:theme code="text.addToCart" var="addToCartText"/>
			<button id="addToCartButton" type="${buttonType}" disabled="true" class="add_to_cart_button positive large <c:if test="${fn:contains(buttonType, 'button')}">out-of-stock</c:if>">
				<spring:theme code="text.addToCart" var="addToCartText"/>
				<spring:theme code="basket.add.to.basket" />
			</button>

			<c:if test="${multiDimensionalProduct}" >
				<sec:authorize ifAnyGranted="ROLE_CUSTOMERGROUP">
					<c:url value="${product.url}/orderForm" var="productOrderFormUrl"/>
					<a href="${productOrderFormUrl}" ><spring:theme code="order.form" /></a>
				</sec:authorize>
			</c:if>
		</div>

	</form>
</div>
