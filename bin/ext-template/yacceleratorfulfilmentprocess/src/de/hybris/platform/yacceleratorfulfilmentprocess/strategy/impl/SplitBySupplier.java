package de.hybris.platform.yacceleratorfulfilmentprocess.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.AbstractSplittingStrategy;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.variants.model.VariantProductModel;

public class SplitBySupplier extends AbstractSplittingStrategy {
	private UserService userService;
	private FlexibleSearchService flexibleSearchService;
	private OrderService orderService;
	private List<InnerOrder> innerOrderList = new ArrayList<InnerOrder>();

	public UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	public OrderService getOrderService() {
		return orderService;
	}

	@Required
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	@Override
	public Object getGroupingObject(AbstractOrderEntryModel orderEntry) {
		System.out.println("method:getGroupingObject**************************************");
		InnerOrder innerOrder = new InnerOrder();
		innerOrder.setOrderEntry(orderEntry);

		List<EmployeeModel> employeeList = findSupplierByProductCode(orderEntry.getProduct().getCode());
		if (!employeeList.isEmpty()) {
			System.out.println(employeeList.size());
			System.out.println(employeeList.get(0).getUid());
			innerOrder.setSupplier(employeeList.get(0));
		} else {
			EmployeeModel employeeModel = new EmployeeModel();
			employeeModel.setUid("");
			innerOrder.setSupplier(employeeModel);
		}

		innerOrderList.add(innerOrder);
		return null;
	}

	@Override
	public void afterSplitting(Object groupingObject, ConsignmentModel createdOne) {
		System.out.println("method:afterSplitting*****************************************");
		System.out.println(innerOrderList.size());

		Set<String> supplierUidSet = new HashSet<String>();
		for (InnerOrder innerOrder : innerOrderList) {
			if (!supplierUidSet.contains(innerOrder.getSupplier().getUid())) {
				System.out.println(innerOrder.getSupplier().getUid());
				supplierUidSet.add(innerOrder.getSupplier().getUid());
			}
		}

		if (supplierUidSet.size() > 1) {
			int number = 0;
			for (String uid : supplierUidSet) {
				++number;
				final OrderModel newOrder = new OrderModel();
				final List<AbstractOrderEntryModel> newEntriesModel = new ArrayList<AbstractOrderEntryModel>();
				newOrder.setEntries(newEntriesModel);
				newOrder.setCode("");
				for (InnerOrder innerOrder : innerOrderList) {
					if (uid.equalsIgnoreCase(innerOrder.getSupplier().getUid())) {
						if (newOrder.getCode().equals("")) {
							/**
							 * order header
							 * */
							System.out.println("order header......");
							AbstractOrderModel order = innerOrder.getOrderEntry().getOrder();

							newOrder.setUser(order.getUser());
							newOrder.setCode(order.getCode() + "-" + number);
							newOrder.setCurrency(order.getCurrency());
							newOrder.setDate(order.getDate());
							newOrder.setNet(order.getNet());
							newOrder.setCalculated(order.getCalculated());
							newOrder.setSite(order.getSite());
							newOrder.setStore(order.getStore());
							newOrder.setB2bcomments(order.getB2bcomments());
							newOrder.setComments(order.getComments());
							newOrder.setConsignments(order.getConsignments());
							for (ConsignmentModel cm : newOrder.getConsignments()) {
								cm.setOrder(newOrder);
							}
							newOrder.setDeliveryAddress(order.getDeliveryAddress());
							newOrder.setDeliveryCost(order.getDeliveryCost());
							newOrder.setPaymentCost(order.getPaymentCost());
							newOrder.setTotalPrice(order.getTotalPrice());
							newOrder.setTotalTax(order.getTotalTax());
							newOrder.setTotalTaxValues(order.getTotalTaxValues());
							newOrder.setStatus(OrderStatus.APPROVED);
							newOrder.setLocale(order.getLocale());
							newOrder.setWorkflow(order.getWorkflow());
						}

						/**
						 * order entries
						 * */
						System.out.println("order entries......");
						AbstractOrderEntryModel entryModel = innerOrder.getOrderEntry();

						final AbstractOrderEntryModel newEntryModel = new OrderEntryModel();
						newEntryModel.setOrder(newOrder);
						newEntryModel.setProduct(entryModel.getProduct());
						newEntryModel.setQuantity(entryModel.getQuantity());
						newEntryModel.setUnit(entryModel.getUnit());
						newEntryModel.setBasePrice(entryModel.getBasePrice());
						newEntryModel.setTotalPrice(entryModel.getTotalPrice());
						newEntryModel.setDeliveryPointOfService(entryModel.getDeliveryPointOfService());
						newEntryModel.setEntryNumber(entryModel.getEntryNumber());
						newEntryModel.setCalculated(entryModel.getCalculated());
						newEntryModel.setConsignmentEntries(entryModel.getConsignmentEntries());
						for (ConsignmentEntryModel cem : newEntryModel.getConsignmentEntries()) {
							cem.setOrderEntry(newEntryModel);
						}
						newEntryModel.setDeliveryAddress(entryModel.getDeliveryAddress());
						newEntryModel.setDeliveryMode(entryModel.getDeliveryMode());
						newEntryModel.setNamedDeliveryDate(entryModel.getNamedDeliveryDate());

						newEntriesModel.add(newEntryModel);
					}
				}

				System.out.println("saveOrder......");
				orderService.saveOrder(newOrder);
			}
		}

		innerOrderList.clear();
	}

	@SuppressWarnings("unchecked")
	private List<EmployeeModel> findSupplierByProductCode(final String productCode) {
		UserModel userModel = userService.getCurrentUser();
		userService.setCurrentUser(userService.getAdminUser());
		final Map<String, Object> values = new HashMap<String, Object>();
		values.put("code", productCode);
		final StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT {s:").append(EmployeeModel.PK).append("} FROM {PlatformSupplier as s join FoodMaterialVariantProduct as p on {s:pk} = {p:supplier}} WHERE {p:").append(VariantProductModel.CODE).append("} = ?code ");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString(), values);
		query.setResultClassList(Collections.singletonList(EmployeeModel.class));
		final SearchResult<EmployeeModel> res = getFlexibleSearchService().search(query);
		final List<EmployeeModel> result = res.getResult();
		userService.setCurrentUser(userModel);
		return result == null ? Collections.EMPTY_LIST : result;
	}

	class InnerOrder {
		private AbstractOrderEntryModel orderEntry;
		private EmployeeModel supplier;

		public AbstractOrderEntryModel getOrderEntry() {
			return orderEntry;
		}

		public void setOrderEntry(AbstractOrderEntryModel orderEntry) {
			this.orderEntry = orderEntry;
		}

		public EmployeeModel getSupplier() {
			return supplier;
		}

		public void setSupplier(EmployeeModel supplier) {
			this.supplier = supplier;
		}

	}

}
