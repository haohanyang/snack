import { ChannelInfo } from './channel'
import User from './user'

export interface Message {
    id: number
    author: User
    channel: ChannelInfo
    content: string
    createdAt: string
    attachmentUri: string | null
}