/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        tf: {
          DEFAULT: '#00AFE1',
          light: '#f0faff',
          dark: '#0084A8',
        }
      }
    },
  },
  plugins: [],
}
