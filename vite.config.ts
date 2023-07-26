import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    root: './src/main/webapp',
    server: {
        port: 8001,
    },
    plugins: [react()],
    build: {
        outDir: '../resources/static',
        rollupOptions: {
            external: ['public'],
            output: {
                entryFileNames: 'js/[name].js',
                chunkFileNames: 'js/[name].js',
                assetFileNames: '[ext]/[name].[ext]',
            }
        }
    }
})
