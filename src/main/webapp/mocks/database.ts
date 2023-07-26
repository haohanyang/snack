import { ChannelInfo, ChannelType, GroupChannel, UserChannel } from "../models/channel"
import User from "../models/user"
import { Message } from "../models/message"
import { fakerSV as faker } from '@faker-js/faker'
import Membership from "../models/membership"

interface GroupChannelData {
    channel: GroupChannel,
    messages: Message[]
    memberships: Membership[]
}

interface UserChannelData {
    channel: UserChannel,
    messages: Message[]
}

export default class Database {
    me: User
    friends: User[] = []
    userChannels: Map<number, UserChannelData> = new Map<number, UserChannelData>()
    groupChannels: Map<number, GroupChannelData> = new Map<number, GroupChannelData>()
    attachments: Map<string, string> = new Map<string, string>() // uuid -> image url

    constructor(friendCount: number = 30, groupCount: number = 10, maxGroupMembers: number = 8, messagesPerChannelCount: number = 20) {
        this.me = Database.createRandomUser("me")

        for (let i = 0; i < friendCount; i++) {
            this.friends.push(Database.createRandomUser())
        }

        // Create user channels
        const contactedFriends = new Set<string>()
        for (let i = 0; i < friendCount / 2; i++) {
            const friend = this.friends[faker.number.int({ min: 0, max: this.friends.length - 1 })]
            if (!contactedFriends.has(friend.id)) {
                contactedFriends.add(friend.id)
                const channel = Database.createUserChannel(this.me, friend)
                // Add messages
                const messages: Message[] = []
                const channelInfo = {
                    id: channel.id,
                    type: ChannelType.USER
                }
                for (let j = 0; j < messagesPerChannelCount; j++) {
                    messages.push({
                        id: Database.createRandomId(),
                        author: Database.createRandomId() % 2 == 0 ? this.me : friend,
                        channel: channelInfo,
                        content: faker.lorem.paragraph(),
                        createdAt: (faker.date.between(
                            {
                                from: messages.length == 0 ? channel.lastUpdated : messages[messages.length - 1].createdAt,
                                to: new Date()
                            })).toISOString(),
                        attachmentUri: faker.number.int({ min: 0, max: 5 }) == 0 ? faker.image.url() : null
                    })
                }

                // Set last messages and unread messages count
                channel.lastMessage = messages[messages.length - 1]
                channel.lastUpdated = messages[messages.length - 1].createdAt
                channel.unreadMessagesCount = faker.number.int({ min: 0, max: messages.length - 1 })

                this.userChannels.set(channel.id, {
                    channel: channel,
                    messages: messages
                })
                contactedFriends.add(friend.id)
            }

        }

        // Create group channels
        for (let i = 0; i < groupCount; i++) {
            const channel = Database.createGroupChannel()

            const groupMemberIds: string[] = []
            const memberships: Membership[] = []

            // Create group members
            groupMemberIds.push(this.me.id)
            memberships.push({
                id: Database.createRandomId(),
                member: this.me,
                isCreator: false
            })

            for (let j = 0; j < maxGroupMembers; j++) {
                const member = j == 0 ? this.me : this.friends[faker.number.int({ min: 0, max: this.friends.length - 1 })]
                if (!groupMemberIds.includes(member.id)) {
                    groupMemberIds.push(member.id)
                    memberships.push({
                        id: Database.createRandomId(),
                        member: member,
                        isCreator: false
                    })
                }
            }

            channel.memberCount = memberships.length
            // Randomly select a creator
            const creatorIndex = faker.number.int({ min: 0, max: memberships.length - 1 })
            memberships[creatorIndex].isCreator = true

            const channelInfo: ChannelInfo = {
                id: channel.id,
                type: ChannelType.GROUP
            }
            // Create group messages
            const messages: Message[] = []
            for (let j = 0; j < messagesPerChannelCount; j++) {
                const authorIndex = faker.number.int({ min: 0, max: memberships.length - 1 })
                messages.push({
                    id: Database.createRandomId(),
                    author: memberships[authorIndex].member,
                    channel: channelInfo,
                    content: faker.lorem.paragraph(),
                    createdAt: (faker.date.between(
                        {
                            from: messages.length == 0 ? channel.createdAt : messages[messages.length - 1].createdAt,
                            to: new Date()
                        })).toISOString(),
                    attachmentUri: faker.number.int({ min: 0, max: 5 }) == 0 ? faker.image.url() : null
                })
            }

            // Set last messages and unread messages count
            channel.lastMessage = messages[messages.length - 1]
            channel.lastUpdated = messages[messages.length - 1].createdAt
            channel.unreadMessagesCount = faker.number.int({ min: 0, max: messages.length - 1 })

            this.groupChannels.set(channel.id, {
                channel: channel,
                messages: messages,
                memberships: memberships
            })
        }
    }

    static createRandomId(upperBound: number = 1000000): number {
        return faker.number.int({ min: 0, max: upperBound })
    }

    getUser(id: string): User | null {
        if (id === this.me.id) {
            return this.me
        }

        for (const friend of this.friends) {
            if (friend.id === id) {
                return friend
            }
        }
        return null
    }

    getUserChannel(user1Id: string, user2Id: string): UserChannel | null {
        const channels = Array.from(this.userChannels.values()).map((data) => data.channel)
        for (const channel of channels) {
            if (channel.user1.id === user1Id && channel.user2.id === user2Id) {
                return channel
            }
        }
        return null
    }

    static createRandomUser(username: string = faker.internet.userName()): User {
        return {
            id: faker.string.uuid(),
            username: username,
            firstName: faker.person.firstName(),
            lastName: faker.person.lastName(),
            avatar: faker.image.avatar(),
            backgroundImage: faker.image.url(),
            bio: faker.lorem.paragraph(),
            status: faker.lorem.sentence()
        }
    }

    static createUserChannel(user1: User, user2: User, time: Date = faker.date.past()): UserChannel {
        return {
            id: Database.createRandomId(),
            type: ChannelType.USER,
            lastMessage: null,
            lastUpdated: time.toISOString(),
            unreadMessagesCount: 0,
            user1: user1.id < user2.id ? user1 : user2,
            user2: user1.id < user2.id ? user2 : user1
        }
    }

    static createGroupChannel(time: Date = faker.date.past()): GroupChannel {

        return {
            id: Database.createRandomId(),
            type: ChannelType.GROUP,
            name: faker.lorem.words(),
            description: faker.lorem.paragraph(),
            lastMessage: null,
            lastUpdated: time.toISOString(),
            createdAt: time.toISOString(),
            unreadMessagesCount: 0,
            backgroundImage: faker.image.url(),
            memberCount: 0,
        }
    }
}