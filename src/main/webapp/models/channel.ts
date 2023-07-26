import { Message } from "./message"
import User from "./user"

export enum ChannelType {
    USER = 0,
    GROUP = 1
}

export interface UserChannel {
    id: number
    type: ChannelType
    lastMessage: Message | null
    lastUpdated: string
    unreadMessagesCount: number
    user1: User
    user2: User
}

export interface GroupChannel {
    id: number
    type: ChannelType
    lastMessage: Message | null
    lastUpdated: string
    unreadMessagesCount: number
    name: string
    backgroundImage: string
    description: string
    createdAt: string
    memberCount: number
}

export interface Channels {
    userChannels: UserChannel[]
    groupChannels: GroupChannel[]
}

export interface ChannelInfo {
    id: number
    type: ChannelType
}
