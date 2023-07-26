import { Navbar, Box, NavLink, Text, Avatar, Alert, Group, Skeleton, Stack, Badge } from "@mantine/core"
import { useNavigate } from "react-router-dom"
import { useGetMeQuery, useGetChannelsQuery } from "../app/api/apiSlice"
import { useMemo } from "react"
import { ChannelType, GroupChannel, UserChannel } from "../models/channel"
import { useAppSelector } from "../app/hooks"

interface LeftNavProps {
    openChat: () => void
}

export default function LeftNav({ openChat }: LeftNavProps) {
    const navigate = useNavigate()

    return <Box>
        <Navbar.Section>
            <NavLink
                onClick={() => {
                    openChat(); navigate("friends")
                }}
                label="Friends" icon={<i className="bi bi-person"></i>}
            />
            <NavLink
                onClick={() => {
                    openChat(); navigate("groups")
                }}
                label="Groups" icon={<i className="bi bi-people"></i>}
            />
            <NavLink
                label="Direct Messages"
                icon={<i className="bi bi-chat-left"></i>}
                childrenOffset={0}
                opened
            >
                <ChannelNav openChat={openChat} />
            </NavLink>

        </Navbar.Section>
    </Box>
}

function ChannelNav({ openChat }: LeftNavProps) {
    const navigate = useNavigate()

    const skeleton = (id: number) => <Group align="start" key={id}>
        <Skeleton height={40} width={40} radius="sm" />
        <Stack mt={5}>
            <Skeleton height={8} width={70} radius="xl" />
            <Skeleton height={8} w={{ lg: 190, md: 140, sm: 130, base: "calc(100vw - 120px)" }} radius="xl" />
        </Stack>
    </Group>

    const currentChannelId = useAppSelector(state => state.chat.channelId)

    const { me } = useGetMeQuery(undefined, {
        selectFromResult: ({ data }) => ({ me: data }),
    })

    const { data: channels = { userChannels: [], groupChannels: [] }, isFetching, isError } = useGetChannelsQuery()

    const sortedChannels = useMemo(() => {
        const { userChannels, groupChannels } = channels
        const allChannels = [...userChannels, ...groupChannels]
        const sortedChannels = allChannels.slice()
        sortedChannels.sort((a, b) => {
            return new Date(b.lastUpdated).getTime() - new Date(a.lastUpdated).getTime()
        })
        return sortedChannels
    }, [channels])

    if (isError) {
        return <Alert color="red">
            An error has occurred
        </Alert>
    }

    if (isFetching || !me) {
        return <Stack spacing="sm" ml="sm">
            {new Array<number>(5).fill(0).map((_, index) => skeleton(index))}
        </Stack>
    }

    return <>
        {sortedChannels.map(channel => {
            if (channel.type == ChannelType.USER) {
                const userChannel = channel as UserChannel
                const id = "u" + userChannel.id
                const contact = userChannel.user1.id == me?.id ? userChannel.user2 : userChannel.user1
                return <NavLink
                    key={id}
                    active={currentChannelId == id}
                    onClick={() => {
                        openChat(); navigate("channels/u/" + userChannel.id)
                    }}
                    label={contact.firstName + " " + contact.lastName}
                    rightSection={userChannel.unreadMessagesCount > 0 && <Badge color="red" variant="filled" size="sm">{userChannel.unreadMessagesCount}</Badge>}
                    description={userChannel.lastMessage ? <Box w={{ lg: 170, md: 125, sm: 120, base: "calc(100vw - 140px)" }}>
                        <Text truncate>{userChannel.lastMessage.content}</Text>
                    </Box> : <span>&#8203;</span>} icon={
                        <Avatar src={contact.avatar}
                            alt="avatar"
                        />}
                />
            } else {
                const { id, name, lastMessage, unreadMessagesCount } = channel as GroupChannel
                return <NavLink
                    key={"g" + id}
                    onClick={
                        () => {
                            openChat(); navigate("channels/g/" + id)
                        }
                    }
                    active={currentChannelId == "g" + id}
                    label={name}
                    rightSection={unreadMessagesCount > 0 && <Badge color="red" variant="filled" size="sm">2</Badge>}
                    description={lastMessage ? <Box w={{ lg: 170, md: 125, sm: 120, base: "calc(100vw - 140px)" }}>
                        <Text truncate>{lastMessage.author.firstName + ": " + lastMessage.content}</Text>
                    </Box> : <span>&#8203;</span>}
                    icon={<Avatar color="blue"><i className="bi bi-people-fill"></i></Avatar>}
                />
            }
        })}
    </>
}
