import { Container } from "@mantine/core"
import UserDetails from "../components/UserDetails"
import GroupDetails from "../components/GroupDetails"
import { useMatch } from "react-router-dom"

interface RightBarProps {
    toggleRightBar: () => void
}

export default function RightBar({ toggleRightBar }: RightBarProps) {

    const matchUserChannel = useMatch("chat/channels/u/:channelId")
    const matchGroupChannel = useMatch("chat/channels/g/:channelId")
    if (matchUserChannel) {
        const { channelId } = matchUserChannel.params as { channelId: string }
        return <UserDetails channelId={channelId} toggleRightBar={toggleRightBar} />
    }

    if (matchGroupChannel) {
        const { channelId } = matchGroupChannel.params as { channelId: string }
        return <GroupDetails channelId={channelId} toggleRightBar={toggleRightBar} />
    }

    return <Container>
    </Container>
}