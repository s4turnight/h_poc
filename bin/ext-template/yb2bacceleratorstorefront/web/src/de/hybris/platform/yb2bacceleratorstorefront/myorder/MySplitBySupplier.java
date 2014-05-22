package de.hybris.platform.yb2bacceleratorstorefront.myorder;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.strategy.AbstractSplittingStrategy;

import java.util.ArrayList;
import java.util.List;


public class MySplitBySupplier extends AbstractSplittingStrategy
{

	private OrderService orderService;

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * @param orderService
	 *           the orderService to set
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	@Override
	public Object getGroupingObject(final AbstractOrderEntryModel orderEntry)
	{
		// YTODO Auto-generated method stub
		System.out.println("method:getGroupingObject...........................");

		final AbstractOrderModel order = orderEntry.getOrder();
		final OrderModel newOrder = new OrderModel();

		newOrder.setCode(orderEntry.getOrder().getCode() + "-" + Math.random() * 10 + Math.random() * 10);
		newOrder.setWorkflow(order.getWorkflow());
		newOrder.setCurrency(order.getCurrency());
		newOrder.setUser(order.getUser());
		newOrder.setB2bcomments(order.getB2bcomments());
		newOrder.setComments(order.getComments());
		newOrder.setConsignments(order.getConsignments());
		newOrder.setDate(order.getDate());
		newOrder.setDeliveryAddress(order.getDeliveryAddress());
		newOrder.setDeliveryCost(order.getDeliveryCost());
		newOrder.setCalculated(order.getCalculated());

		final List<AbstractOrderEntryModel> entriesModel = orderEntry.getOrder().getEntries();
		final List<AbstractOrderEntryModel> newEntriesModel = new ArrayList<AbstractOrderEntryModel>();

		for (final AbstractOrderEntryModel entryModel : entriesModel)
		{
			final AbstractOrderEntryModel newEntryModel = new OrderEntryModel();

			newEntryModel.setProduct(entryModel.getProduct());
			newEntryModel.setQuantity(entryModel.getQuantity());
			newEntryModel.setUnit(entryModel.getUnit());
			newEntryModel.setEntryNumber(entryModel.getEntryNumber());
			newEntryModel.setCalculated(entryModel.getCalculated());
			newEntryModel.setConsignmentEntries(entryModel.getConsignmentEntries());
			newEntryModel.setDeliveryAddress(entryModel.getDeliveryAddress());
			newEntryModel.setDeliveryMode(entryModel.getDeliveryMode());
			newEntryModel.setNamedDeliveryDate(entryModel.getNamedDeliveryDate());

			newEntriesModel.add(newEntryModel);
		}

		newOrder.setEntries(newEntriesModel);
		orderService.saveOrder(newOrder);

		return null;
	}

	@Override
	public void afterSplitting(final Object groupingObject, final ConsignmentModel createdOne)
	{
		// YTODO Auto-generated method stub	
		System.out.println("method:afterSplitting...........................");
	}
}
