
import { Text, Group, Avatar, Box, Menu, ActionIcon, Flex, Alert } from "@mantine/core"
import { useGetGroupChannelQuery } from "../app/api/apiSlice"
import StatusSkeleton from "./StatusSkeleton"

interface GroupStatusProps {
    channelId: string
    toggleButtons: JSX.Element
}

export default function GroupStatus({ channelId, toggleButtons }: GroupStatusProps) {

    const { data: channel, isFetching, isError } = useGetGroupChannelQuery(channelId)

    if (isError) {
        return <Alert color="red">An error occurred</Alert>
    }

    if (isFetching || !channel) {
        return <StatusSkeleton />
    }

    return <Group style={{ height: "100%" }}>
        <Avatar color="blue"><i className="bi bi-people-fill"></i></Avatar>
        <Box sx={{ flex: 1 }}>
            <Text size="md" weight={500}>
                {channel.name}
            </Text>
            <Text color="dimmed" size="sm">
                {channel.memberCount + " members"}
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