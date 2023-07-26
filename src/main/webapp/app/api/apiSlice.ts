import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react"
import User from "../../models/user"
import Membership from "../../models/membership"
import { ChannelInfo, ChannelType, Channels, GroupChannel, UserChannel } from "../../models/channel"
import { Message } from "../../models/message"
import { GroupChannelRequest, MessageRequest, UpdateProfileRequest, UserChannelRequest } from "../../models/requests"
import { Client } from "@stomp/stompjs"
import SockJS from "sockjs-client"

const baseUrl = process.env.NODE_ENV === "server_mock" ? "http://localhost:8080/api/v1" : "/api/v1"

export const apiSlice = createApi({
    reducerPath: "api",
    baseQuery: fetchBaseQuery({
        baseUrl: baseUrl,
        prepareHeaders: (headers) => {
            if (process.env.NODE_ENV === "server_mock") {
                const token = localStorage.getItem("access_token")
                if (!token) {
                    console.error("No access token found")
                }
                headers.set("Authorization", `Bearer ${token}`)
            }
            return headers
        },
    }),

    tagTypes: ["Me", "Friends", "NavChannels", "GroupChannel", "GroupChannels", "UserChannel", "UserChannels", "ChannelMessages", "GroupChannelMembers"],
    endpoints: builder => ({
        // The currently logged in user
        getMe: builder.query<User, void>({
            query: () => "users/@me",
            providesTags: ["Me"],
            async onCacheEntryAdded(undefined, { dispatch, cacheDataLoaded, cacheEntryRemoved }) {
                if (process.env.NODE_ENV === "browser_mock") {
                    return
                }
                const stompClient = new Client({
                    webSocketFactory: () => {
                        if (process.env.NODE_ENV === "server_mock") {
                            return new SockJS("http://localhost:8080/ws")
                        } else {
                            return new SockJS("/ws")
                        }
                    }
                })
                try {
                    const { data: me } = await cacheDataLoaded
                    stompClient.onConnect = _frame => {
                        stompClient.subscribe(`/gateway/${me.id}`, packet => {
                            const message: Message = JSON.parse(packet.body)
                            // Don't notify if the message is sent by me
                            if (message.author.id === me.id) {
                                return
                            }
                            // Use browser notification api
                            if ("Notification" in window) {
                                if (Notification.permission === "granted") {
                                    new Notification(message.author.firstName + ": " + message.content)
                                } else {
                                    Notification.requestPermission().then(permission => {
                                        if (permission == "granted") {
                                            new Notification(message.author.firstName + ": " + message.content)
                                        }
                                    })
                                }
                            }
                            console.log("Received message: ", message)
                            // Update message list
                            dispatch(apiSlice.util.updateQueryData("getChannelMessages", message.channel, draft => {
                                draft.push(message)
                            }))
                            // Update channel list
                            dispatch(apiSlice.util.updateQueryData("getChannels", undefined, draft => {
                                if (message.channel.type == ChannelType.USER) {
                                    const channel = draft.userChannels.find(channel => channel.id == message.channel.id)
                                    if (channel) {
                                        channel.lastMessage = message
                                        channel.lastUpdated = message.createdAt
                                        channel.unreadMessagesCount++
                                    } else {
                                        // dispatch(apiSlice.util.invalidateTags(["NavChannels"]))
                                        dispatch(apiSlice.util.updateQueryData("getChannels", undefined, draft => {
                                            const userChannel: UserChannel = {
                                                id: message.channel.id,
                                                user1: message.author.id < me.id ? message.author : me,
                                                user2: message.author.id < me.id ? me : message.author,
                                                lastMessage: message,
                                                lastUpdated: new Date(message.createdAt).toISOString(),
                                                unreadMessagesCount: 1,
                                                type: ChannelType.USER
                                            }
                                            draft.userChannels.push(userChannel)
                                        }))
                                    }
                                } else {
                                    const channel = draft.groupChannels.find(channel => channel.id == message.channel.id)
                                    if (channel) {
                                        channel.lastMessage = message
                                        channel.lastUpdated = message.createdAt
                                        channel.unreadMessagesCount++
                                    } else {
                                        // TODO: get group channel info
                                        dispatch(apiSlice.util.invalidateTags(["NavChannels"]))
                                    }
                                }
                            }))
                        })
                    }
                    stompClient.activate()
                } catch (error) {
                    console.error(error)
                } finally {
                    await cacheEntryRemoved
                    stompClient.deactivate()
                }
            },
        }),
        updateMe: builder.mutation<User, UpdateProfileRequest>({
            query: (request: UpdateProfileRequest) => ({
                url: "users/@me",
                method: "PATCH",
                body: request
            }),
            onQueryStarted: async (_arg, { dispatch, queryFulfilled }) => {
                try {
                    const { data: updatedUser } = await queryFulfilled
                    dispatch(apiSlice.util.updateQueryData("getMe", undefined, draft => {
                        draft.firstName = updatedUser.firstName
                        draft.lastName = updatedUser.lastName
                        draft.backgroundImage = updatedUser.backgroundImage
                        draft.avatar = updatedUser.avatar
                        draft.bio = updatedUser.bio
                        draft.status = updatedUser.status
                    }))
                } catch (error) {
                }
            },
        }),
        // All friends of the user
        getFriends: builder.query<User[], void>({
            query: () => "users/@me/friends",
            providesTags: ["Friends"]
        }),
        // All channels the user is a member of
        getChannels: builder.query<Channels, void>({
            query: () => "users/@me/channels",
            providesTags: ["NavChannels"]
        }),
        // Create a new user channel
        addNewUserChannel: builder.mutation<UserChannel, UserChannelRequest>({
            query: (userChannelRequest: UserChannelRequest) => ({
                url: "channels/user",
                method: "POST",
                body: userChannelRequest,
            }),
            invalidatesTags: ["NavChannels"]
        }),
        // Get a specific user channel
        getUserChannel: builder.query<UserChannel, string>({
            query: (channelId: string) => `channels/user/${channelId}`,
            providesTags: (_result, _error, arg) => [{ type: "UserChannel", id: arg }]
        }),
        // Get all messages for a specific channel
        getChannelMessages: builder.query<Message[], ChannelInfo>({
            query: ({ id, type }) =>
                type == ChannelType.USER ? `channels/user/${id}/messages` : `channels/group/${id}/messages`,
            providesTags: (_result, _error, arg) =>
                [{ type: "ChannelMessages", id: arg.type == ChannelType.USER ? "u" + arg.id : "g" + arg.id }],
        }),
        addNewGroupChannel: builder.mutation<GroupChannel, GroupChannelRequest>({
            query: (groupChannelRequest: GroupChannelRequest) => ({
                url: "channels/group",
                method: "POST",
                body: groupChannelRequest,
            }),
            invalidatesTags: ["NavChannels", "GroupChannels"]
        }),
        // Get all group channels the user is a member of
        getGroupChannels: builder.query<GroupChannel[], void>({
            query: () => "users/@me/channels/group",
            providesTags: ["GroupChannels"]
        }),
        // Get a specific group channel
        getGroupChannel: builder.query<GroupChannel, string>({
            query: (channelId: string) => `channels/group/${channelId}`,
            providesTags: (_result, _error, arg) => [{ type: "GroupChannel", id: arg }]
        }),
        getGroupChannelMembers: builder.query<Membership[], number>({
            query: (channelId: number) => `channels/group/${channelId}/members`,
            providesTags: (_result, _error, arg) => [{ type: "GroupChannelMembers", id: arg }]
        }),
        // Send a message to a channel
        sendChannelMessage: builder.mutation<Message, MessageRequest>({
            query: (messageRequest) => {
                const url = messageRequest.channel.type === ChannelType.USER ? `channels/user/${messageRequest.channel.id}/messages`
                    : `channels/group/${messageRequest.channel.id}/messages`
                return {
                    url: url,
                    method: "POST",
                    body: messageRequest,
                }
            },
            async onQueryStarted(messageRequest, { dispatch, queryFulfilled }) {
                // In the mock environment, update the query cache immediately instead of 
                // waiting for the incoming websocket message
                try {
                    const { data: newMessage } = await queryFulfilled
                    dispatch(apiSlice.util.updateQueryData("getChannelMessages",
                        messageRequest.channel, draft => { draft.push(newMessage) }))
                    dispatch(apiSlice.util.updateQueryData("getChannels", undefined, draft => {
                        if (messageRequest.channel.type == ChannelType.USER) {
                            const channel = draft.userChannels.find(channel => channel.id == messageRequest.channel.id)
                            if (channel) {
                                channel.lastMessage = newMessage
                                channel.lastUpdated = newMessage.createdAt
                            } else {
                                dispatch(apiSlice.util.invalidateTags(["NavChannels"]))
                            }
                        } else {
                            const channel = draft.groupChannels.find(channel => channel.id == messageRequest.channel.id)
                            if (channel) {
                                channel.lastMessage = newMessage
                                channel.lastUpdated = newMessage.createdAt
                            } else {
                                dispatch(apiSlice.util.invalidateTags(["NavChannels"]))
                            }
                        }
                    }))
                } catch (err) {
                    console.error(err)
                }

            },
        }),
    }),
})

export const {
    useGetMeQuery, useGetChannelsQuery, useUpdateMeMutation,
    useGetUserChannelQuery, useGetGroupChannelQuery, useGetChannelMessagesQuery,
    useGetGroupChannelsQuery, useGetFriendsQuery, useAddNewUserChannelMutation,
    useAddNewGroupChannelMutation, useSendChannelMessageMutation, useGetGroupChannelMembersQuery
} = apiSlice
