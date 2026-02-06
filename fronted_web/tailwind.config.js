/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'tf': '#00AFE1',
        'tf-light': '#f0faff',
        'tf-dark': '#0084A8',
      },
    },
  },
  plugins: [],
}
