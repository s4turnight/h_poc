/**
 * 
 */
package de.hybris.platform.yb2bacceleratorcore.platform;

import de.hybris.platform.product.impl.DefaultVariantsService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.variants.jalo.VariantProduct;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.yb2bacceleratorcore.model.SupplierEmployeeModel;


/**
 * @author I074791
 * 
 */
public class PlatformVariantsService extends DefaultVariantsService
{
	@Override
	public Object getVariantAttributeValue(final VariantProductModel variant, final String qualifier)
	{
		try
		{
			final Object obj = getModelService().getAttributeValue(variant, qualifier);
			if (obj instanceof SupplierEmployeeModel)
			{
				return ((SupplierEmployeeModel) obj).getName();
			}
			else
			{
				return obj;
			}

		}
		catch (final AttributeNotSupportedException e)
		{
			final VariantProduct variantproduct = getModelService().getSource(variant);
			try
			{
				return getModelService().toModelLayer(variantproduct.getAttribute(qualifier));
			}
			catch (final Exception ex)
			{
				throw new SystemException("cannot read variant attribute value for '" + qualifier + "' due to : " + ex.getMessage(),
						ex);
			}
		}
	}
}
