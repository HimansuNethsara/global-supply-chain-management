<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/25/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Order Details</title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

    <!-- Tailwind CSS Output -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/output.css">
</head>
<body class="dashboard-container font-inter">

<!-- Background Glow Orbs -->
<div class="fixed inset-0 z-0 pointer-events-none overflow-hidden">
    <div class="glow-orb w-[600px] h-[600px] -top-[200px] -right-[200px] bg-red-500/5"></div>
    <div class="glow-orb w-[400px] h-[400px] -bottom-[100px] -left-[100px] bg-red-900/5"></div>
    <div class="glow-orb w-[300px] h-[300px] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-500/3"></div>
</div>

<!-- ===== HEADER ===== -->
<jsp:include page="/WEB-INF/views/header.jsp" />

<!-- ===== SIDEBAR ===== -->
<jsp:include page="/WEB-INF/views/sidebar.jsp" />

<!-- ===== MAIN CONTENT WRAPPER ===== -->
<div class="main-content-wrapper">
    <main class="main-content relative z-10">
        <div class="p-4 sm:p-6 lg:p-8 max-w-4xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/customers/orders" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Order Details</h1>
                    <p class="text-white/30 text-sm font-light">View order information</p>
                </div>
                <div class="ml-auto flex items-center gap-2">
                    <c:if test="${order.status == 'PENDING'}">
                        <button onclick="editOrder(${order.orderId})" class="px-4 py-2 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all flex items-center gap-2">
                            <i class="fas fa-pen"></i>
                            <span>Edit</span>
                        </button>
                        <button onclick="cancelOrder(${order.orderId})" class="px-4 py-2 bg-red-500/20 border border-red-500/30 rounded-lg text-red-400 text-sm font-medium hover:bg-red-500/30 transition-all flex items-center gap-2">
                            <i class="fas fa-times"></i>
                            <span>Cancel</span>
                        </button>
                    </c:if>
                </div>
            </div>

            <!-- ===== ORDER INFO ===== -->
            <div class="dashboard-card mb-6">
                <div class="flex items-start justify-between mb-4">
                    <h2 class="text-white/60 text-sm font-semibold">Order #${order.orderId}</h2>
                    <c:choose>
                        <c:when test="${order.status == 'FULFILLED'}">
                            <span class="transaction-status completed">Fulfilled</span>
                        </c:when>
                        <c:when test="${order.status == 'ASSIGNED'}">
                            <span class="transaction-status pending">Assigned</span>
                        </c:when>
                        <c:when test="${order.status == 'CANCELLED'}">
                            <span class="transaction-status failed">Cancelled</span>
                        </c:when>
                        <c:otherwise>
                            <span class="transaction-status pending">${order.status}</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="text-white/30 text-xs block">Destination</label>
                        <div class="text-white/80 text-base font-medium">${order.destination}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Customer</label>
                        <div class="text-white/80 text-base font-medium">${order.customer.user.fullName}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Requested Date</label>
                        <div class="text-white/80 text-base font-medium">
                            ${order.requestedDate != null ? order.requestedDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Notes</label>
                        <div class="text-white/80 text-base font-medium">${order.notes != null ? order.notes : '—'}</div>
                    </div>
                </div>
            </div>

            <!-- ===== ORDER ITEMS ===== -->
            <div class="dashboard-card">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                        <i class="fas fa-boxes text-dracula-red"></i>
                        Order Items
                    </h3>
                    <c:if test="${order.status == 'PENDING'}">
                        <button onclick="editOrder(${order.orderId})" class="text-dracula-red text-xs hover:text-red-400 transition-all">
                            <i class="fas fa-plus mr-1"></i>Add Items
                        </button>
                    </c:if>
                </div>

                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>SKU</th>
                            <th>Item Name</th>
                            <th>Quantity</th>
                            <th>Unit Price</th>
                            <th>Total</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty orderItems}">
                                <c:forEach var="item" items="${orderItems}">
                                    <tr>
                                        <td class="text-white/60 text-xs font-medium">${item.item.sku}</td>
                                        <td class="text-white/50 text-xs">${item.item.name}</td>
                                        <td class="text-white/60 text-xs">${item.quantity}</td>
                                        <td class="text-white/60 text-xs">LKR ${item.item.unitPrice}</td>
                                        <td class="text-white/60 text-xs font-medium">LKR ${item.quantity * item.item.unitPrice}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center text-white/20 py-4">
                                        <span class="text-xs">No items in this order</span>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                        <tfoot>
                        <tr>
                            <td colspan="4" class="text-right text-white/60 text-xs font-medium">Total:</td>
                            <td class="text-white/80 text-sm font-bold">LKR ${orderTotal}</td>
                        </tr>
                        </tfoot>
                    </table>
                </div>

                <!-- ===== ASSOCIATED SHIPMENTS ===== -->
                <c:if test="${not empty shipments}">
                    <div class="mt-4 pt-4 border-t border-white/5">
                        <h4 class="text-white/40 text-xs font-semibold uppercase tracking-wider mb-2">Associated Shipments</h4>
                        <div class="flex flex-wrap gap-2">
                            <c:forEach var="s" items="${shipments}">
                                <a href="${pageContext.request.contextPath}/shipments/details?shipmentId=${s.shipmentId}"
                                   class="px-3 py-1.5 bg-white/5 border border-white/10 rounded-lg text-white/50 text-xs hover:bg-white/10 hover:text-white transition-all">
                                        ${s.trackingNumber}
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/orders.js"></script>
</body>
</html>