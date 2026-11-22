package lk.jiat.global.interceptor;

import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.service.CustomerOrderServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.util.AppLogger;

import jakarta.inject.Inject;
import java.util.List;

@Interceptor
@StatusChange
public class ShipmentStatusUpdateInterceptor {

    @Inject
    private CustomerOrderServiceLocal customerOrderService;

    @Inject
    private ShipmentServiceLocal shipmentService;

    @AroundInvoke
    public Object interceptStatusUpdate(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();

        try {
            // Check if this is a status update method
            String methodName = ctx.getMethod().getName();

            // Only intercept updateShipmentStatus method
            if ("updateShipmentStatus".equals(methodName)) {
                Object[] params = ctx.getParameters();
                if (params != null && params.length >= 2) {
                    Integer shipmentId = (Integer) params[0];
                    String newStatus = (String) params[1];

                    // If status changed to DELIVERED, fulfill all linked orders
                    if ("DELIVERED".equals(newStatus) && shipmentId != null) {
                        try {
                            List<CustomerOrder> linkedOrders = customerOrderService.getOrdersByShipment(shipmentId);
                            for (CustomerOrder order : linkedOrders) {
                                if ("ASSIGNED".equals(order.getStatus())) {
                                    customerOrderService.fulfillOrder(order.getOrderId());
                                    AppLogger.info("Order {} fulfilled via shipment {} delivery",
                                            order.getOrderId(), shipmentId);
                                }
                            }
                        } catch (Exception e) {
                            AppLogger.error("Failed to fulfill linked orders for shipment {}: {}", shipmentId, e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            AppLogger.error("Error in ShipmentStatusUpdateInterceptor: {}", e.getMessage());
        }

        return result;
    }
}