import { FileUploadResult } from "./file"
import { ChannelInfo } from "./channel"

export interface UserChannelRequest {
    user1Id: string,
    user2Id: string,
}

export interface GroupChannelRequest {
    name: string,
    description: string,
    memberIds: string[],
    creatorId: string,
}

export interface MessageRequest {
    channel: ChannelInfo,
    authorId: string,
    content: string,
    fileUploadResult: FileUploadResult | null,
}

export interface UpdateProfileRequest {
    userId: string,
    firstName: string,
    lastName: string,
    bio: string,
    status: string,
    avatar: FileUploadResult | null,
    backgroundImage: FileUploadResult | null,
}