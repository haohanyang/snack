import { useMatch } from "react-router"
import UserStatus from "../components/UserStatus"
import GroupStatus from "../components/GroupStatus"
import { ActionIcon, Group } from "@mantine/core"

interface AppHeaderProps {
    toggleLeftBar: () => void
    toggleRightBar: () => void
}

export default function AppHeader({ toggleLeftBar, toggleRightBar }: AppHeaderProps) {
    const matchUserChannel = useMatch("chat/channels/u/:channelId")
    const matchGroupChannel = useMatch("chat/channels/g/:channelId")

    const toggleButtons = <>
        <ActionIcon onClick={toggleLeftBar} variant="transparent" display={{ sm: "none" }}  >
            <i className="bi bi-box-arrow-right"></i>
        </ActionIcon>
        <ActionIcon onClick={toggleRightBar} variant="transparent" display={{ sm: "none" }} >
            <i className="bi bi-box-arrow-left"></i>
        </ActionIcon>
    </>

    if (matchUserChannel) {
        const { channelId } = matchUserChannel.params as { channelId: string }
        return <UserStatus channelId={channelId} toggleButtons={toggleButtons} />
    }

    if (matchGroupChannel) {
        const { channelId } = matchGroupChannel.params as { channelId: string }
        return <GroupStatus channelId={channelId} toggleButtons={toggleButtons} />
    }
    return <Group sx={{ height: "100%" }}>
        <ActionIcon onClick={toggleLeftBar} variant="transparent" >
            <i className="bi bi-list" style={{ fontSize: 25 }}></i>
        </ActionIcon>
    </Group>


}