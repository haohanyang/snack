import { Card, Avatar, Text, rem, useMantineTheme, Paper, Divider, Alert, ActionIcon, Skeleton } from "@mantine/core"
import { useGetMeQuery, useGetUserChannelQuery } from "../app/api/apiSlice"


interface UserDetailsProps {
    channelId: string
    toggleRightBar: () => void
}

export default function UserDetails({ channelId, toggleRightBar }: UserDetailsProps) {
    const theme = useMantineTheme()

    const skeleton = <Card sx={{ backgroundColor: theme.colors.gray[1], height: "100%" }} p={0}>
        <Skeleton height={200} />
        <Skeleton width={80} height={80} circle mt={-30} ml={20} sx={{ border: `${rem(2)} solid ${theme.white}` }} />
        <Skeleton height={150} m={10} />
        <Skeleton height={150} m={10} />
    </Card>

    const { me } = useGetMeQuery(undefined, {
        selectFromResult: ({ data }) => ({ me: data }),
    })
    const { data: channel, isFetching, isError } = useGetUserChannelQuery(channelId)

    if (isError) {
        return <Alert color="red">An error occurred</Alert>
    }

    if (isFetching || !channel || !me) {
        return skeleton
    }

    const user = channel.user1.id == me.id ? channel.user2 : channel.user1

    return <Card sx={{ backgroundColor: theme.colors.gray[1], height: "100%" }}>
        <Card.Section sx={{ backgroundImage: `url(${user.backgroundImage})`, backgroundSize: "cover", height: 200 }}>
            <ActionIcon variant="default" display={{ sm: "none" }} onClick={toggleRightBar}>
                <i className="bi bi-x" style={{ fontSize: 25 }}></i>
            </ActionIcon>
        </Card.Section>
        <Avatar src={user.avatar} size={80} radius={80} mt={-30} sx={{ border: `${rem(2)} solid ${theme.white}` }} />
        <Paper px="sm">
            <Text fz="lg" fw={500} mt="sm">
                {user.firstName + " " + user.lastName}
            </Text>
            <Text fz="sm" c="dimmed">
                {"@" + user.username}
            </Text>
            <Text fz="sm">{user.status}</Text>
            <Divider />
            <Text tt="uppercase">About me</Text>
            <Text fz="sm" c="dimmed">{user.bio}</Text>
        </Paper>
    </Card>
}