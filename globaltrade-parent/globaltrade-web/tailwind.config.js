module.exports = {
    content: [
        "./src/main/webapp/**/*.jsp",
        "./src/main/webapp/WEB-INF/views/**/*.jsp",
    ],
    theme: {
        extend: {
            colors: {
                'dracula-black': '#050505',
                'dracula-dark': '#0d0d0d',
                'dracula-red': '#dc143c',
                'dracula-red-dark': '#8b0000',
                'dracula-red-light': '#ff1744',
            },
            fontFamily: {
                'inter': ['Inter', 'sans-serif'],
            },
            keyframes: {
                imageZoom: {
                    '0%': { transform: 'scale(1.05)' },
                    '100%': { transform: 'scale(1.15)' },
                },
                orbFloat: {
                    '0%, 100%': { transform: 'translate(0, 0) scale(1)' },
                    '33%': { transform: 'translate(50px, -30px) scale(1.1)' },
                    '66%': { transform: 'translate(-30px, 50px) scale(0.9)' },
                },
                bgPulse: {
                    '0%, 100%': { opacity: '0.5' },
                    '50%': { opacity: '1' },
                },
                fadeIn: {
                    '0%': { opacity: '0', transform: 'translateY(10px)' },
                    '100%': { opacity: '1', transform: 'translateY(0)' },
                },
                glowPulse: {
                    '0%, 100%': { boxShadow: '0 0 30px rgba(220, 20, 60, 0.05)' },
                    '50%': { boxShadow: '0 0 40px rgba(220, 20, 60, 0.15)' },
                },
                arrowBounce: {
                    '0%, 100%': { transform: 'translateX(0)' },
                    '50%': { transform: 'translateX(6px)' },
                },
                shimmer: {
                    '0%': { transform: 'translateX(-100%) skewX(-15deg)' },
                    '100%': { transform: 'translateX(100%) skewX(-15deg)' },
                },
                successPulse: {
                    '0%': { transform: 'scale(1)' },
                    '50%': { transform: 'scale(1.02)' },
                    '100%': { transform: 'scale(1)' },
                },
                glowPulseOut: {
                    '0%': {
                        opacity: '1',
                        transform: 'scale(1)',
                        boxShadow: '0 0 0 rgba(220, 20, 60, 0)',
                        borderColor: 'rgba(255,255,255,0.06)',
                    },
                    '30%': {
                        boxShadow: '0 0 80px rgba(220, 20, 60, 0.5), 0 0 150px rgba(220, 20, 60, 0.2)',
                        borderColor: 'rgba(220, 20, 60, 0.4)',
                    },
                    '100%': {
                        opacity: '0',
                        transform: 'scale(0.96)',
                        boxShadow: '0 0 120px rgba(220, 20, 60, 0.3), 0 0 200px rgba(220, 20, 60, 0.1)',
                        borderColor: 'rgba(220, 20, 60, 0.1)',
                    },
                },
                glowPulseIn: {
                    '0%': {
                        opacity: '0',
                        transform: 'scale(0.96)',
                        boxShadow: '0 0 120px rgba(220, 20, 60, 0.4), 0 0 200px rgba(220, 20, 60, 0.15)',
                        borderColor: 'rgba(220, 20, 60, 0.3)',
                    },
                    '40%': {
                        opacity: '0.8',
                        transform: 'scale(1.01)',
                        boxShadow: '0 0 60px rgba(220, 20, 60, 0.25), 0 0 100px rgba(220, 20, 60, 0.1)',
                    },
                    '100%': {
                        opacity: '1',
                        transform: 'scale(1)',
                        boxShadow: '0 0 0 rgba(220, 20, 60, 0)',
                        borderColor: 'rgba(255,255,255,0.06)',
                    },
                },
            },
            animation: {
                imageZoom: 'imageZoom 20s ease-in-out infinite alternate',
                orbFloat: 'orbFloat 15s ease-in-out infinite',
                bgPulse: 'bgPulse 8s ease-in-out infinite',
                fadeIn: 'fadeIn 0.5s ease forwards',
                glowPulse: 'glowPulse 1.5s ease-in-out infinite',
                arrowBounce: 'arrowBounce 0.6s ease infinite',
                shimmer: 'shimmer 1.5s ease-in-out infinite',
                successPulse: 'successPulse 0.6s ease',
                glowPulseOut: 'glowPulseOut 0.5s cubic-bezier(0.4, 0, 0.2, 1) forwards',
                glowPulseIn: 'glowPulseIn 0.5s cubic-bezier(0.4, 0, 0.2, 1) forwards',
            },
            borderWidth: {
                '3': '3px',
            },
        },
    },
    plugins: [require("daisyui")],
    daisyui: {
        themes: [
            {
                'dracula': {
                    'primary': '#dc143c',
                    'secondary': '#8b0000',
                    'accent': '#ff1744',
                    'neutral': '#0d0d0d',
                    'base-100': '#050505',
                    'base-200': '#1a1a1a',
                    'base-300': '#2a2a2a',
                    'base-content': '#ffffff',
                },
            },
        ],
        darkTheme: 'dracula',
        themeRoot: 'dracula',
    },
}