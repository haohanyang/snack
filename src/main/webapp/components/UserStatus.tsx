import { Text, Group, Avatar, Box, Menu, ActionIcon, Flex, Alert } from "@mantine/core"

import { useGetMeQuery, useGetUserChannelQuery } from "../app/api/apiSlice"
import StatusSkeleton from "./StatusSkeleton"

interface UserStatusProps {
    channelId: string
    toggleButtons: JSX.Element
}

export default function UserStatus({ channelId, toggleButtons }: UserStatusProps) {

    const { data: channel, isFetching, isError } = useGetUserChannelQuery(channelId)
    const { me } = useGetMeQuery(undefined, {
        selectFromResult: ({ data }) => ({ me: data }),
    })

    if (isError) {
        return <Alert color="red">An error occurred</Alert>
    }

    if (isFetching || !channel || !me) {
        return <StatusSkeleton />
    }

    const user = channel.user1.id == me?.id ? channel.user2 : channel.user1

    return <Group style={{ height: "100%" }}>
        <Avatar
            src={user.avatar}
        />
        <Box sx={{ flex: 1 }}>
            <Text size="md" weight={500}>
                {user.firstName + " " + user.lastName}
            </Text>
            <Text color="dimmed" size="sm">
                {"@" + user.username}
            </Text>
        </Box>
        <Flex>
            <Menu shadow="md" width={200}>
                <Menu.Target>
                    <ActionIcon variant="transparent">
                        <i className="bi bi-three-dots-vertical"></i>
                    </ActionIcon>
                </Menu.Target>

                <Menu.Dropdown>
                    <Menu.Label>Application</Menu.Label>
                    <Menu.Item
                        rightSection={<Text size="xs" color="dimmed">⌘K</Text>}
                    >
                        Search
                    </Menu.Item>
                    <Menu.Divider />
                    <Menu.Label>Danger zone</Menu.Label>
                </Menu.Dropdown>
            </Menu>
            {toggleButtons}
        </Flex>
    </Group>
}