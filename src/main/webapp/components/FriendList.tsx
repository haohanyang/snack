import { Avatar, NavLink, Box, Center, Loader, Alert, ActionIcon, ScrollArea, TextInput, Divider, Text, Group } from "@mantine/core"
import User from "../models/user"

import { useNavigate } from "react-router-dom"
import { useAddNewUserChannelMutation, useGetFriendsQuery, useGetMeQuery } from "../app/api/apiSlice"
import { UserChannelRequest } from "../models/requests"
import { useAppDispatch } from "../app/hooks"
import { useEffect } from "react"
import { reset } from "../app/chat/chatSlice"

export default function FriendList() {

    const dispatch = useAppDispatch()

    useEffect(() => {
        dispatch(reset())
    }, [dispatch])
    const navigate = useNavigate()

    const { me } = useGetMeQuery(undefined, {
        selectFromResult: ({ data }) => ({ me: data }),
    })
    const { data: friends = [], isFetching, isError } = useGetFriendsQuery()
    const [addNewUserChannel, { isLoading }] = useAddNewUserChannelMutation()

    const createChat = async (friend: User) => {
        if (me && !isLoading) {
            try {
                const reqBody: UserChannelRequest = me.id < friend.id ? {
                    user1Id: me.id,
                    user2Id: friend.id
                } : { user1Id: friend.id, user2Id: me.id }
                const channel = await addNewUserChannel(reqBody).unwrap()
                navigate("/chat/channels/u/" + channel.id)
            } catch (err) {
                console.error(err)
            }
        }
    }

    if (isError) {
        return <Alert color="red">An error occurred</Alert>
    }

    if (isFetching) {
        return <Center>
            <Loader />
        </Center>
    }

    return <Box>
        <TextInput mb="sm" placeholder="Search" rightSection={<i className="bi bi-search"></i>} />
        <Group position="apart" px="sm">
            <Text tt="uppercase" fz="sm">{"All friends -" + friends.length}</Text>
            <ActionIcon variant="transparent">
                <i className="bi bi-plus-circle"></i>
            </ActionIcon>
        </Group>
        <Divider m="xs" />
        <ScrollArea h={"calc(100vh - 200px)"}>
            {
                friends.map(user => {
                    return <NavLink
                        key={user.id}
                        component="div"
                        label={user.firstName + " " + user.lastName}
                        description={"@" + user.username}
                        icon={<Avatar src={user.avatar} />}
                        rightSection={
                            <ActionIcon variant="transparent" onClick={() => createChat(user)}>
                                <i className="bi bi-chat-left-fill"></i>
                            </ActionIcon>}
                    />
                })
            }
        </ScrollArea>
    </Box>

}
