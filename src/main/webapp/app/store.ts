import { configureStore } from "@reduxjs/toolkit"
import chatReducer from "./chat/chatSlice"
import { apiSlice } from "./api/apiSlice"

const store = configureStore({
    reducer: {
        chat: chatReducer,
        [apiSlice.reducerPath]: apiSlice.reducer
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware().concat(apiSlice.middleware)

})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

export default store
