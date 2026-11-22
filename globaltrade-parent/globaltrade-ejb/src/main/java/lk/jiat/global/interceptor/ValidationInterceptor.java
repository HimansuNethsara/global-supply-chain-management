package lk.jiat.global.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lk.jiat.global.exception.ValidationException;

import java.lang.reflect.Method;
import java.math.BigDecimal;

@Interceptor
@Validated
public class ValidationInterceptor {

    @AroundInvoke
    public Object validate(InvocationContext ctx) throws Exception {
        // Check if it's an inventory write operation
        Method method = ctx.getMethod();
        String methodName = method.getName();
        Object[] params = ctx.getParameters();

        // For createItem: sku, name, warehouseId, quantity, reorderLevel, unitPrice
        if (methodName.equals("createItem") && params.length >= 6) {
            String sku = (String) params[0];
            String name = (String) params[1];
            Integer warehouseId = (Integer) params[2];
            Integer quantity = (Integer) params[3];
            Integer reorderLevel = (Integer) params[4];
            BigDecimal unitPrice = (BigDecimal) params[5];

            if (sku == null || sku.trim().isEmpty()) {
                throw new ValidationException("SKU is required");
            }
            if (sku.length() > 50) {
                throw new ValidationException("SKU must not exceed 50 characters");
            }

            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Item name is required");
            }
            if (name.length() > 150) {
                throw new ValidationException("Item name must not exceed 150 characters");
            }

            if (warehouseId == null || warehouseId <= 0) {
                throw new ValidationException("Valid warehouse is required");
            }

            if (quantity == null || quantity < 0) {
                throw new ValidationException("Quantity must be 0 or greater");
            }

            if (reorderLevel == null || reorderLevel < 0) {
                throw new ValidationException("Reorder level must be 0 or greater");
            }

            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Unit price must be 0 or greater");
            }

        }

        // For updateItem: itemId, sku, name, warehouseId, quantity, reorderLevel, unitPrice
        if (methodName.equals("updateItem") && params.length >= 7) {
            Integer itemId = (Integer) params[0];
            String sku = (String) params[1];
            String name = (String) params[2];
            Integer warehouseId = (Integer) params[3];
            Integer quantity = (Integer) params[4];
            Integer reorderLevel = (Integer) params[5];
            BigDecimal unitPrice = (BigDecimal) params[6];

            if (itemId == null || itemId <= 0) {
                throw new ValidationException("Invalid item ID");
            }

            if (sku != null && !sku.trim().isEmpty() && sku.length() > 50) {
                throw new ValidationException("SKU must not exceed 50 characters");
            }

            if (warehouseId != null && warehouseId <= 0) {
                throw new ValidationException("Invalid warehouse ID");
            }

            if (quantity != null && quantity < 0) {
                throw new ValidationException("Quantity must be 0 or greater");
            }

            if (reorderLevel != null && reorderLevel < 0) {
                throw new ValidationException("Reorder level must be 0 or greater");
            }

            if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Unit price must be 0 or greater");
            }

        }

        return ctx.proceed();
    }
}