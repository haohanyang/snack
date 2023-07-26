import { rest } from "msw"
import Database from "./database"
import { GroupChannelRequest, MessageRequest, UpdateProfileRequest, UserChannelRequest } from "../models/requests"
import { Channels } from "../models/channel"
import { FileUploadResult } from "../models/file"
import { faker } from "@faker-js/faker"
import Membership from "../models/membership"
import { Message } from "../models/message"

const database = new Database()

const authHandlers = [
    rest.get("/api/v1/users/@me", (_req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(database.me))
    })]

const userHandlers = [
    // Fetch a user's profile
    rest.get("/api/v1/users/:userId", (req: any, res, ctx) => {
        const userId = req.params.userId
        if (!userId || !/\d+/.test(userId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid user id" }))
        }
        const user = database.getUser(userId)
        if (user == null) {
            return res(
                ctx.status(404),
                ctx.json({ message: "User not found" }))
        }
        return res(
            ctx.status(200),
            ctx.json(user))
    }),

    // Update a user's profile
    rest.patch("/api/v1/users/@me", async (req, res, ctx) => {
        const request: UpdateProfileRequest = await req.json()
        database.me.firstName = request.firstName
        database.me.lastName = request.lastName
        database.me.bio = request.bio
        database.me.status = request.status
        if (request.avatar) {
            database.me.avatar = request.avatar.key
        }
        if (request.backgroundImage) {
            database.me.backgroundImage = request.backgroundImage.key
        }
        return res(
            ctx.status(200),
            ctx.json(database.me))
    }),

    // Fetch the user's friends
    rest.get("/api/v1/users/@me/friends", (_, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(database.friends))
    }),

    // Fetch the user's groups
    rest.get("/api/v1/users/@me/groups", (_, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(Array.from(database.groupChannels.values()).map(e => e.channel)))
    })
]

const channelHandlers = [
    rest.get("/api/v1/users/@me/channels", (_, res, ctx) => {
        const channels: Channels = {
            userChannels: Array.from(database.userChannels.values()).map(channelData => channelData.channel),
            groupChannels: Array.from(database.groupChannels.values()).map(channelData => channelData.channel)
        }
        return res(
            ctx.status(200),
            ctx.json(channels))
    }),
]

const userChannelHandlers = [
    // Create a new channel
    rest.post("/api/v1/channels/user", async (req, res, ctx) => {
        const reqBody: UserChannelRequest = await req.json()
        const channel = database.getUserChannel(reqBody.user1Id, reqBody.user2Id)
        if (channel !== null) {
            return res(
                ctx.status(200),
                ctx.json(channel))
        } else {
            const user1 = database.getUser(reqBody.user1Id)
            const user2 = database.getUser(reqBody.user2Id)
            if (user1 == null || user2 == null) {
                return res(
                    ctx.status(401),
                    ctx.json({ message: "User not found" }))
            }
            const newChannel = Database.createUserChannel(user1, user2, new Date())
            database.userChannels.set(newChannel.id, {
                channel: newChannel,
                messages: []
            })
            return res(
                ctx.status(201),
                ctx.json(newChannel))
        }
    }),

    // Fetch a channel
    rest.get("/api/v1/channels/user/:channelId", (req: any, res, ctx) => {
        const channelId = req.params.channelId
        if (!channelId || !/\d+/.test(channelId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid channel id" }))
        }
        const channelData = database.userChannels.get(parseInt(channelId))
        if (!channelData) {
            return res(
                ctx.status(404),
                ctx.json({ message: "Channel not found" }))
        } else {
            const { channel } = channelData
            return res(
                ctx.status(200),
                ctx.json(channel))
        }
    }),

    // Fetch channel messages
    rest.get("/api/v1/channels/user/:channelId/messages", (req: any, res, ctx) => {
        const channelId = req.params.channelId
        if (!channelId || !/\d+/.test(channelId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid channel id" }))
        }
        const channelData = database.userChannels.get(parseInt(channelId))
        if (!channelData) {
            return res(
                ctx.status(404),
                ctx.json({ message: "Channel not found" }))
        } else {
            const { messages } = channelData
            return res(
                ctx.status(200),
                ctx.json(messages))
        }
    }),

    // Send a message to the channel
    rest.post("/api/v1/channels/user/:channelId/messages", (req: any, res, ctx) => {
        const channelId = req.params.channelId
        if (!channelId || !/\d+/.test(channelId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid channel id" }))
        }
        const channelData = database.userChannels.get(parseInt(channelId))
        if (!channelData) {
            return res(
                ctx.status(401),
                ctx.json({ message: "Channel doesn't exist" }))
        } else {
            return req.json().then((messageRequest: MessageRequest) => {
                const { channel, messages } = channelData
                const author = database.getUser(messageRequest.authorId)
                if (!author) {
                    return res(
                        ctx.status(401),
                        ctx.json({ message: "User not found" }))
                }
                const message: Message = {
                    id: Database.createRandomId(),
                    author: author,
                    channel: messageRequest.channel,
                    content: messageRequest.content,
                    createdAt: (new Date()).toISOString(),
                    attachmentUri: messageRequest.fileUploadResult?.key ?? null
                }

                messages.push(message)
                channel.lastMessage = message
                channel.lastUpdated = message.createdAt
                return res(
                    ctx.status(201),
                    ctx.json(message))
            })
        }
    }),
]

const groupChannelHandlers = [
    // Fetch all channels
    rest.get("/api/v1/users/@me/channels/group", (_req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(Array.from(database.groupChannels.values()).map(channelData => channelData.channel)))
    }),

    // Fetch a channel
    rest.get("/api/v1/channels/group/:channelId", (req: any, res, ctx) => {
        const channelId = req.params.channelId
        if (!channelId || !/\d+/.test(channelId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid channel id" }))
        }
        const channelData = database.groupChannels.get(parseInt(channelId))
        if (!channelData) {
            return res(
                ctx.status(404),
                ctx.json({ message: "Channel doesn't exist" }))
        } else {
            const { channel } = channelData
            return res(
                ctx.status(200),
                ctx.json(channel))
        }
    }),

    // Fetch the member list
    rest.get("/api/v1/channels/group/:channelId/members", (req: any, res, ctx) => {
        const channelId = req.params.channelId
        if (!channelId || !/\d+/.test(channelId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid channel id" }))
        }
        const channelData = database.groupChannels.get(parseInt(channelId))
        if (!channelData) {
            return res(
                ctx.status(404),
                ctx.json({ message: "Channel doesn't exist" }))
        } else {
            const { memberships } = channelData
            return res(
                ctx.status(200),
                ctx.json(memberships))
        }
    }),

    // Create a new channel
    rest.post("/api/v1/channels/group", async (req, res, ctx) => {
        const request: GroupChannelRequest = await req.json()
        const creator = database.getUser(request.creatorId)
        if (!creator) {
            return res(
                ctx.status(401),
                ctx.json({ message: "User not found" }))
        }

        const memberships: Membership[] = [{
            id: Database.createRandomId(),
            member: creator,
            isCreator: true,
        }]
        for (const memberId of request.memberIds) {
            const member = database.getUser(memberId)
            if (member !== null) {
                memberships.push({
                    id: Database.createRandomId(),
                    member: member,
                    isCreator: false,
                })
            }
        }

        const channel = Database.createGroupChannel(new Date())
        channel.name = request.name
        channel.description = request.description
        channel.backgroundImage = faker.image.url()
        channel.memberCount = memberships.length

        database.groupChannels.set(channel.id, {
            channel: channel,
            messages: [],
            memberships: memberships,
        })

        return res(
            ctx.status(201),
            ctx.json(channel))
    }),

    // Fetch channel messages
    rest.get("/api/v1/channels/group/:channelId/messages", (req: any, res, ctx) => {
        const channelId = req.params.channelId
        if (!channelId || !/\d+/.test(channelId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid channel id" }))
        }
        const channelData = database.groupChannels.get(parseInt(channelId))
        if (!channelData) {
            return res(
                ctx.status(404),
                ctx.json({ message: "Channel doesn't exist" }))
        } else {
            const { messages } = channelData
            return res(
                ctx.status(200),
                ctx.json(messages))
        }
    }),

    // Send a message to the channel
    rest.post("/api/v1/channels/group/:channelId/messages", (req: any, res, ctx) => {
        const channelId = req.params.channelId
        if (!channelId || !/\d+/.test(channelId)) {
            return res(
                ctx.status(400),
                ctx.json({ message: "Invalid channel id" }))
        }
        const channelData = database.groupChannels.get(parseInt(channelId))
        if (!channelData) {
            return res(
                ctx.status(401),
                ctx.json({ message: "Channel doesn't exist" }))
        } else {
            return req.json().then((messageRequest: MessageRequest) => {
                const { channel, messages } = channelData
                const author = database.getUser(messageRequest.authorId)
                if (!author) {
                    return res(
                        ctx.status(401),
                        ctx.json({ message: "User not found" }))
                }
                const message: Message = {
                    id: Database.createRandomId(),
                    author: author,
                    channel: messageRequest.channel,
                    content: messageRequest.content,
                    createdAt: (new Date()).toISOString(),
                    attachmentUri: messageRequest.fileUploadResult?.key ?? null
                }

                messages.push(message)
                channel.lastMessage = message
                channel.lastUpdated = message.createdAt
                return res(
                    ctx.status(201),
                    ctx.json(message))
            })
        }
    }),
]

const fileHandlers = [
    rest.post("/api/v1/files", (req: any, res, ctx) => {
        const formData = req.body as { file: File, user: string }
        const result: FileUploadResult = {
            bucket: faker.lorem.word(),
            key: faker.string.uuid(),
            fileName: formData.file.name,
            size: formData.file.size,
            contentType: formData.file.type,
            uri: ""
        }
        return res(
            ctx.status(201),
            ctx.json(result))
    })
]

export const handlers = [
    ...authHandlers, ...userHandlers, ...channelHandlers,
    ...userChannelHandlers, ...groupChannelHandlers, ...fileHandlers,
]
