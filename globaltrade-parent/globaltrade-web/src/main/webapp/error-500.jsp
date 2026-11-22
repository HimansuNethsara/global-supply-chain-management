<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Internal Server Error</title>

    <!-- Font Awesome CDN -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #0a0a0a;
            padding: 1rem;
        }
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
            background: rgba(0, 0, 0, 0.88);
            z-index: 1;
        }
        @keyframes imageZoom {
            0% { transform: scale(1.05); }
            100% { transform: scale(1.15); }
        }
        .card {
            position: relative;
            z-index: 10;
            max-width: 550px;
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
        .top-bar {
            width: calc(100% + 80px);
            height: 4px;
            background: linear-gradient(to right, #ef4444, #dc2626);
            border-radius: 16px 16px 0 0;
            margin: -48px -40px 32px -40px;
            padding: 0 40px;
        }
        .icon-wrapper {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: rgba(239, 68, 68, 0.1);
            border: 1px solid rgba(239, 68, 68, 0.2);
            margin-bottom: 20px;
        }
        .icon-wrapper i {
            font-size: 40px;
            color: #ef4444;
        }
        .error-code {
            font-size: 72px;
            font-weight: 800;
            color: #ef4444;
            letter-spacing: 0.05em;
            margin-bottom: 8px;
        }
        .title {
            font-size: 28px;
            font-weight: 700;
            color: #ffffff;
            margin-bottom: 8px;
        }
        .subtitle {
            font-size: 15px;
            color: rgba(255, 255, 255, 0.5);
            line-height: 1.6;
            margin-bottom: 12px;
        }
        .error-id {
            font-size: 12px;
            color: rgba(255, 255, 255, 0.15);
            font-family: monospace;
            margin-bottom: 20px;
        }
        .btn-group {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            justify-content: center;
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
        .footer {
            margin-top: 24px;
            padding-top: 16px;
            border-top: 1px solid rgba(255, 255, 255, 0.05);
            font-size: 11px;
            color: rgba(255, 255, 255, 0.1);
        }
        .footer i {
            color: rgba(239, 68, 68, 0.3);
            margin-right: 4px;
        }
        @media (max-width: 480px) {
            .card { padding: 32px 20px; }
            .top-bar { margin: -32px -20px 24px -20px; width: calc(100% + 40px); }
            .error-code { font-size: 52px; }
            .title { font-size: 22px; }
        }
    </style>
</head>
<body>

<div class="bg-wrapper">
    <div class="bg-image"></div>
    <div class="overlay"></div>
</div>

<div class="card">
    <div class="top-bar"></div>

    <div class="icon-wrapper">
        <i class="fas fa-exclamation-triangle"></i>
    </div>

    <div class="error-code">500</div>

    <h1 class="title">Internal Server Error</h1>
    <p class="subtitle">Something went wrong on our end. We're working to fix it.</p>

    <div class="error-id" id="errorId">
        <i class="fas fa-code"></i> Error ID: <span id="errorIdValue">---</span>
    </div>

    <div class="btn-group">
        <a href="${pageContext.request.contextPath}/dashboard" class="btn-primary">
            <i class="fas fa-home"></i> Go Home
        </a>
        <button onclick="window.location.reload()" class="btn-secondary">
            <i class="fas fa-sync-alt"></i> Try Again
        </button>
    </div>

    <div class="footer">
        <i class="fas fa-shield-halved"></i> Protected under Sri Lanka PDPA No. 9 of 2022
    </div>
</div>

<script>
    // Generate a random error ID
    document.addEventListener('DOMContentLoaded', function() {
        const errorId = 'ERR-' + Date.now().toString(36).toUpperCase() + '-' +
            Math.random().toString(36).substring(2, 6).toUpperCase();
        document.getElementById('errorIdValue').textContent = errorId;
    });
</script>

</body>
</html>