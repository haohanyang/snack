import {
    Card,
    Avatar,
    Text,
    useMantineTheme,
    List,
    Badge,
    Divider,
    ScrollArea,
    Center,
    Loader,
    Alert,
    Box,
    ActionIcon,
    Skeleton
} from "@mantine/core"
import { GroupChannel } from "../models/channel"
import { useState } from "react"
import { useGetGroupChannelQuery, useGetGroupChannelMembersQuery } from "../app/api/apiSlice"

interface GroupDetailsProps {
    channelId: string
    toggleRightBar: () => void
}

interface MemberComponentProps {
    channel: GroupChannel
}

export default function GroupDetails({ channelId, toggleRightBar }: GroupDetailsProps) {

    const theme = useMantineTheme()

    const skeleton = <Card sx={{ backgroundColor: theme.colors.gray[1], height: "100%" }} p={0}>
        <Skeleton height={200} />
        <Skeleton height={150} m={10} />
        <Divider />
        <Skeleton height={150} m={10} />
    </Card>

    const [memberCount, _setMemberCount] = useState(0)

    const { data: channel, isFetching, isError } = useGetGroupChannelQuery(channelId)

    if (isError) {
        return <Alert color="red">An error occurred</Alert>
    }

    if (isFetching || !channel) {
        return skeleton
    }

    return <Card sx={{ backgroundColor: theme.colors.gray[1], height: "100%" }}>
        <Card.Section sx={{ backgroundImage: `url(${channel.backgroundImage})`, backgroundSize: "cover", height: 200 }}>
            <ActionIcon variant="transparent" display={{ sm: "none" }} onClick={toggleRightBar} p={10}>
                <i className="bi bi-x" style={{ fontSize: 25 }}></i>
            </ActionIcon>
        </Card.Section>

        <Box pb="sm">
            <Text fz="lg" fw={500} mt="sm">
                {channel.name}
            </Text>
            <Text fz="sm" c="dimmed">
                {channel.description}
            </Text>
        </Box>

        <Divider />
        <Text fz="sm" fw={500} mb="sm" tt="uppercase">{"Members -" + (memberCount == 0 ? "" : memberCount)}</Text>
        <ScrollArea h={400}>
            <MemberComponent channel={channel} />
        </ScrollArea>

    </Card>
}

function MemberComponent({ channel }: MemberComponentProps) {

    const { data: memberships = [], isFetching, isError } = useGetGroupChannelMembersQuery(channel.id)

    if (isError) {
        return <Alert color="red">An error occurred</Alert>
    }

    if (isFetching) {
        return <Center>
            <Loader />
        </Center>
    }

    return <List
        spacing="xs"
        size="sm"
        center
    >
        {
            memberships.map(({ id, member, isCreator }) =>
                <List.Item
                    icon={<Avatar src={member.avatar} />}
                    key={id}
                >
                    <Text>{member.firstName + " " + member.lastName}
                        {isCreator && <Badge color="yellow">Admin</Badge>}
                    </Text>
                    <Text size="sm" color="dimmed">{"@" + member.username}</Text>
                </List.Item>
            )
        }
    </List>

}