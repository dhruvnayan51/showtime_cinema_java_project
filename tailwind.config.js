/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        midnight: {
          950: '#0B0B12',
          900: '#10101A',
          800: '#17172A',
          700: '#1F1F38',
          600: '#2A2A4A',
        },
        electric: {
          400: '#946BFF',
          500: '#7848FF',
          600: '#602BFF',
        },
        violet: {
          400: '#C084FC',
          500: '#A855F7',
          600: '#9333EA',
        },
        coral: {
          400: '#FF6F7E',
          500: '#FF5364',
          600: '#E63946',
        },
        gold: {
          400: '#FFD370',
          500: '#FFC857',
          600: '#E5A830',
        }
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
        display: ['Outfit', 'Plus Jakarta Sans', 'sans-serif']
      },
      boxShadow: {
        'glow-purple': '0 0 25px -5px rgba(120, 72, 255, 0.4)',
        'glow-coral': '0 0 25px -5px rgba(255, 83, 100, 0.4)',
        'glow-gold': '0 0 25px -5px rgba(255, 200, 87, 0.35)',
        'glass': '0 8px 32px 0 rgba(0, 0, 0, 0.37)'
      },
      animation: {
        'pulse-slow': 'pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'float': 'float 3s ease-in-out infinite',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-6px)' },
        }
      }
    },
  },
  plugins: [],
}
