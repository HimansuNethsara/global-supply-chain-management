<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found</title>

    <!-- Font Awesome CDN -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <style>
        /* ===== RESET ===== */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #0a0a0a;
            padding: 1rem;
        }

        /* ===== BACKGROUND ===== */
        .bg-wrapper {
            position: fixed;
            inset: 0;
            z-index: 0;
            overflow: hidden;
        }

        .bg-wrapper .bg-image {
            position: absolute;
            inset: 0;
            background-image: url('${pageContext.request.contextPath}/images/login-background.jpg');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            transform: scale(1.05);
            animation: imageZoom 20s ease-in-out infinite alternate;
        }

        .bg-wrapper .overlay {
            position: absolute;
            inset: 0;
            background: rgba(0, 0, 0, 0.85);
            z-index: 1;
        }

        @keyframes imageZoom {
            0% { transform: scale(1.05); }
            100% { transform: scale(1.15); }
        }

        /* ===== CARD ===== */
        .card {
            position: relative;
            z-index: 10;
            max-width: 600px;
            width: 100%;
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.05);
            border-radius: 16px;
            padding: 48px 40px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
            text-align: center;
        }

        /* ===== TOP BAR ===== */
        .top-bar {
            width: 100%;
            height: 4px;
            background: linear-gradient(to right, #3b82f6, #7c3aed);
            border-radius: 16px 16px 0 0;
            margin: -48px -40px 32px -40px;
            padding: 0 40px;
            width: calc(100% + 80px);
        }

        /* ===== ICON ===== */
        .icon-wrapper {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: rgba(59, 130, 246, 0.1);
            border: 1px solid rgba(59, 130, 246, 0.2);
            margin-bottom: 20px;
        }

        .icon-wrapper i {
            font-size: 40px;
            color: #60a5fa;
        }

        /* ===== ERROR CODE ===== */
        .error-code {
            font-size: 72px;
            font-weight: 800;
            background: linear-gradient(to right, #60a5fa, #a78bfa);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: 0.05em;
            margin-bottom: 8px;
        }

        /* ===== TITLE ===== */
        .title {
            font-size: 28px;
            font-weight: 700;
            color: #ffffff;
            margin-bottom: 12px;
        }

        /* ===== DESCRIPTION ===== */
        .description {
            font-size: 16px;
            color: rgba(255, 255, 255, 0.5);
            line-height: 1.6;
            margin-bottom: 24px;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }

        /* ===== BUTTON GROUP ===== */
        .btn-group {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            justify-content: center;
            margin-bottom: 24px;
        }

        .btn-primary {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 28px;
            background: linear-gradient(to right, #3b82f6, #7c3aed);
            color: #ffffff;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 0 30px rgba(59, 130, 246, 0.3);
        }

        .btn-secondary {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 28px;
            background: rgba(255, 255, 255, 0.05);
            color: rgba(255, 255, 255, 0.5);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            font-size: 15px;
            font-weight: 500;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.1);
            color: rgba(255, 255, 255, 0.7);
        }

        /* ===== QUICK LINKS ===== */
        .quick-links {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            justify-content: center;
            margin-bottom: 20px;
        }

        .quick-link {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 16px;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 50px;
            color: rgba(255, 255, 255, 0.4);
            font-size: 12px;
            text-decoration: none;
            transition: all 0.2s ease;
        }

        .quick-link:hover {
            background: rgba(255, 255, 255, 0.1);
            color: rgba(255, 255, 255, 0.7);
        }

        .quick-link i {
            font-size: 12px;
        }

        /* ===== DIVIDER ===== */
        .divider {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 16px 0 20px 0;
        }

        .divider-line {
            flex: 1;
            height: 1px;
            background: rgba(255, 255, 255, 0.05);
        }

        .divider-text {
            font-size: 11px;
            color: rgba(255, 255, 255, 0.15);
            text-transform: uppercase;
            letter-spacing: 0.1em;
            white-space: nowrap;
        }

        /* ===== FOOTER ===== */
        .footer {
            margin-top: 16px;
            padding-top: 16px;
            border-top: 1px solid rgba(255, 255, 255, 0.05);
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 8px;
            font-size: 11px;
            color: rgba(255, 255, 255, 0.1);
        }

        .footer i {
            color: rgba(59, 130, 246, 0.3);
            margin-right: 4px;
        }

        /* ===== RESPONSIVE ===== */
        @media (max-width: 480px) {
            .card {
                padding: 32px 20px;
            }
            .top-bar {
                margin: -32px -20px 24px -20px;
                width: calc(100% + 40px);
            }
            .error-code {
                font-size: 52px;
            }
            .title {
                font-size: 22px;
            }
            .description {
                font-size: 14px;
            }
            .btn-primary, .btn-secondary {
                padding: 10px 20px;
                font-size: 13px;
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>

<!-- ===== BACKGROUND ===== -->
<div class="bg-wrapper">
    <div class="bg-image"></div>
    <div class="overlay"></div>
</div>

<!-- ===== CARD ===== -->
<div class="card">
    <div class="top-bar"></div>

    <!-- Icon -->
    <div class="icon-wrapper">
        <i class="fas fa-map-signs"></i>
    </div>

    <!-- Error Code -->
    <div class="error-code">404</div>

    <!-- Title -->
    <h1 class="title">Page Not Found</h1>

    <!-- Description -->
    <p class="description">
        The page you're looking for doesn't exist or has been moved.
        It might have been removed, renamed, or is temporarily unavailable.
    </p>

    <!-- Quick Links -->
    <div class="quick-links">
        <a href="${pageContext.request.contextPath}/dashboard" class="quick-link">
            <i class="fas fa-th-large"></i> Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/shipments" class="quick-link">
            <i class="fas fa-truck"></i> Shipments
        </a>
        <a href="${pageContext.request.contextPath}/inventory" class="quick-link">
            <i class="fas fa-warehouse"></i> Inventory
        </a>
        <a href="${pageContext.request.contextPath}/vendors" class="quick-link">
            <i class="fas fa-building"></i> Vendors
        </a>
    </div>

    <!-- Buttons -->
    <div class="btn-group">
        <a href="${pageContext.request.contextPath}/dashboard" class="btn-primary">
            <i class="fas fa-home"></i> Go Home
        </a>
        <button onclick="window.history.back()" class="btn-secondary">
            <i class="fas fa-undo"></i> Go Back
        </button>
    </div>

    <!-- Divider -->
    <div class="divider">
        <span class="divider-line"></span>
        <span class="divider-text">Need Help?</span>
        <span class="divider-line"></span>
    </div>

    <!-- Footer -->
    <div class="footer">
        <span><i class="fas fa-shield-halved"></i> Protected under Sri Lanka PDPA No. 9 of 2022</span>
        <span>•</span>
        <span>ERROR 404</span>
    </div>
</div>

</body>
</html>