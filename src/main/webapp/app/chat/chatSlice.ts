import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface ChatState {
    channelId: string | null,
}

const initialState: ChatState = {
    channelId: null,
}

const groupChatSlice = createSlice({
    name: "chat",
    initialState,
    reducers: {
        reset(state) {
            state.channelId = null
        },
        setId(state, action: PayloadAction<string>) {
            state.channelId = action.payload
        },
    },
})

export const { setId, reset } = groupChatSlice.actions
export default groupChatSlice.reducer