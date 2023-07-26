import { Group, Avatar, Text, Menu, Loader, UnstyledButton } from "@mantine/core"

import { useGetMeQuery } from "../app/api/apiSlice"
import ProfileSetting from "./ProfileSetting"
import { useDisclosure } from "@mantine/hooks"

export default function UserMenu() {
    const { me } = useGetMeQuery(undefined, {
        selectFromResult: ({ data }) => ({ me: data }),
    })

    const [opened, { open, close }] = useDisclosure(false)

    if (!me) {
        return <Loader />
    }

    return <>
        <Menu shadow="md" width={220}>
            <Menu.Target>
                <UnstyledButton>
                    <Group>
                        <Avatar src={me.avatar}></Avatar>
                        <div>
                            <Text>{me.firstName + " " + me.lastName}</Text>
                            <Text size="xs" color="dimmed">{"@" + me.username}</Text>
                        </div>
                    </Group>
                </UnstyledButton>
            </Menu.Target>
            <Menu.Dropdown>
                <Menu.Item icon={<i className="bi bi-gear"></i>} onClick={open} >Setting</Menu.Item>
                <Menu.Item icon={<i className="bi bi-person"></i>}>Log out</Menu.Item>
            </Menu.Dropdown>
        </Menu>
        <ProfileSetting opened={opened} onClose={close} />
    </>


    // return <Group>
    //     < Avatar src={me.avatar} />
    //     <Box sx={{ flex: 1 }}>
    //         <Text size="md">
    //             {me.firstName + " " + me.lastName}
    //         </Text>
    //         <Text color="dimmed" size="sm">
    //             {"@" + me?.username}
    //         </Text>
    //     </Box>
    //     <Menu shadow="md" width={220}>
    //         <Menu.Target>
    //             <ActionIcon variant="transparent">
    //                 <i className="bi bi-three-dots-vertical" style={{ fontSize: 20 }}></i>
    //             </ActionIcon>
    //         </Menu.Target>
    //         <Menu.Dropdown>
    //             <Menu.Item icon={<i className="bi bi-gear" onClick={() => {
    //                 modals.open({
    //                     title: 'Setting',
    //                     children: <ProfileSetting />,
    //                 })
    //             }}></i>}>Setting</Menu.Item>
    //             <Menu.Item icon={<i className="bi bi-person"></i>}>Log out</Menu.Item>
    //         </Menu.Dropdown>
    //     </Menu>
    // </Group >
}